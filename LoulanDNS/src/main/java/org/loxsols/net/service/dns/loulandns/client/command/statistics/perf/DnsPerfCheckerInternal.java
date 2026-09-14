package org.loxsols.net.service.dns.loulandns.client.command.statistics.perf;

import java.util.*;
import java.util.concurrent.*;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;

import java.util.*;
import java.util.concurrent.*;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;

import java.util.*;
import java.util.concurrent.*;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;

import java.util.*;
import java.util.concurrent.*;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;

public class DnsPerfCheckerInternal {

    // デフォルト値
    private static String targetServer = "127.0.0.254";
    private static int targetPort = 53;
    private static int concurrency = 1;
    private static int totalRequests = 10;
    private static long timeoutMs = 1000; // タイムアウト時間(ms)

    private static final int QTYPE_A = 1;
    private static final int QCLASS_IN = 1;

    private static final String[] DOMAIN_POOL = {
        "google.com", "cloudflare.com", "github.com", "microsoft.com",
        "amazon.com", "wikipedia.org", "yahoo.co.jp", "apple.com",
        "cman.jp", "dns.google", "openai.com", "bing.com"
    };

    private static final String[] SUBDOMAIN_PREFIXES = {
        "www", "api", "mail", "dev", "status", "ns1", ""
    };

    public static void main(String[] args) {
        parseArguments(args);

        System.out.println("=== 実行パラメータ ===");
        System.out.printf("  Target Server : %s:%d\n", targetServer, targetPort);
        System.out.printf("  Concurrency   : %d\n", concurrency);
        System.out.printf("  Total Requests: %d\n", totalRequests);
        System.out.printf("  Timeout Limit : %d ms\n", timeoutMs);
        System.out.println("======================");

        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        List<Future<Integer>> futures = new ArrayList<>();
        Random random = new Random();

        ThreadLocal<IDNSLookupClient> clientThreadLocal = ThreadLocal.withInitial(() -> {
            try {
                IDNSLookupClient client = new SimpleUDPResolverImpl();

                client.setDNSServerAddress(targetServer);
                client.setDNSServerPort(targetPort);
                client.setEnableEDNS(true);
                return client;
            } catch (Exception e) {
                throw new RuntimeException("IDNSLookupClient の初期化に失敗しました", e);
            }
        });

        // 1. タスクの投入 (純粋に resolve の完了時間を計測)
        for (int i = 0; i < totalRequests; i++) {
            String domain = generateRandomDomain(random);
            futures.add(executor.submit(() -> {
                IDNSLookupClient client = clientThreadLocal.get();
                long start = System.nanoTime();
                IDNSResponseMessage  response = client.resolve(domain, QTYPE_A, QCLASS_IN);
                int time = (int) ((System.nanoTime() - start) / 1_000_000);

                String result = String.format("%s   , op=%d  , time=%d", domain, response.getDNSHeaderSection().getOPCode(), time );
                System.out.println(result);

                return time;
            }));
        }

        // 2. 結果の回収 (引数で指定された timeoutMs を動的適用)
        List<Integer> queryTimes = new ArrayList<>();
        int timeoutCount = 0;
        int errorCount = 0;

        for (Future<Integer> future : futures) {
            try {
                Integer time = future.get(timeoutMs, TimeUnit.MILLISECONDS);
                queryTimes.add(time);
            } catch (TimeoutException e) {
                timeoutCount++;
                future.cancel(true);
            } catch (ExecutionException e) {
                errorCount++;
                System.err.println("[ERROR] resolve実行中に例外発生:");
                e.getCause().printStackTrace(); // 根本原因のスタックトレースを出力
            } catch (Exception e) {
                errorCount++;
                System.err.println("[ERROR] 予期せぬ例外: " + e.getMessage());
                e.printStackTrace();
            }
        }

        executor.shutdown();

        // 3. 統計結果の表示
        printStatistics(queryTimes, timeoutCount, errorCount);
    }

    private static void parseArguments(String[] args)
    {

        if (args.length == 0)
        {
            printUsageAndExit();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if ((arg.equals("-s") || arg.equals("--server")) && i + 1 < args.length) {
                targetServer = args[++i];
            } else if ((arg.equals("-p") || arg.equals("--port")) && i + 1 < args.length) {
                targetPort = Integer.parseInt(args[++i]);
            } else if ((arg.equals("-c") || arg.equals("--concurrency")) && i + 1 < args.length) {
                concurrency = Integer.parseInt(args[++i]);
            } else if ((arg.equals("-n") || arg.equals("--requests")) && i + 1 < args.length) {
                totalRequests = Integer.parseInt(args[++i]);
            } else if ((arg.equals("-t") || arg.equals("--timeout")) && i + 1 < args.length) {
                timeoutMs = Long.parseLong(args[++i]);
            } else if (arg.equals("-h") || arg.equals("--help")) {
                printUsageAndExit();
            }
        }

        // 位置指定形式 (IP Port Concurrency Requests TimeoutMs)
        if (!args[0].startsWith("-")) {
            if (args.length >= 1) targetServer = args[0];
            if (args.length >= 2) targetPort = Integer.parseInt(args[1]);
            if (args.length >= 3) concurrency = Integer.parseInt(args[2]);
            if (args.length >= 4) totalRequests = Integer.parseInt(args[3]);
            if (args.length >= 5) timeoutMs = Long.parseLong(args[4]);
        }
    }

    private static void printUsageAndExit() {
        System.out.println("Usage:");
        System.out.println("  位置指定形式 : java DnsPerfCheckerInternal [IP] [Port] [Concurrency] [Requests] [TimeoutMs]");
        System.out.println("  フラグ形式   : java DnsPerfCheckerInternal -s <IP> -p <Port> -c <Concurrency> -n <Requests> -t <TimeoutMs>");
        System.out.println("Example:");
        System.out.println("  java DnsPerfCheckerInternal 127.0.0.254 53 10 1000 500");
        System.out.println("  java DnsPerfCheckerInternal -s 127.0.0.254 -p 53 -c 20 -n 1000 -t 1500");
        System.exit(0);
    }

    private static String generateRandomDomain(Random random) {
        String base = DOMAIN_POOL[random.nextInt(DOMAIN_POOL.length)];
        String prefix = SUBDOMAIN_PREFIXES[random.nextInt(SUBDOMAIN_PREFIXES.length)];
        return prefix.isEmpty() ? base : prefix + "." + base;
    }

    private static void printStatistics(List<Integer> queryTimes, int timeoutCount, int errorCount) {
        int successCount = queryTimes.size();

        System.out.println("\n=== DNS 内部API性能測定 統計レポート ===");
        System.out.printf("Target Server  : %s:%d\n", targetServer, targetPort);
        System.out.printf("Total Sent     : %d\n", totalRequests);
        System.out.printf("Successful     : %d\n", successCount);
        System.out.printf("Timeout (>=%dms): %d\n", timeoutMs, timeoutCount);
        System.out.printf("Error          : %d\n", errorCount);

        if (queryTimes.isEmpty()) {
            System.out.printf("\n[INFO] %d ms 未満で成功したクエリが存在しないため、レイテンシ統計を出力できません。\n", timeoutMs);
            return;
        }

        Collections.sort(queryTimes);

        int min = queryTimes.get(0);
        int max = queryTimes.get(successCount - 1);
        double avg = queryTimes.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        int p50Index = (int) Math.floor(successCount * 0.50);
        int p95Index = Math.min((int) Math.floor(successCount * 0.95), successCount - 1);
        int p99Index = Math.min((int) Math.floor(successCount * 0.99), successCount - 1);

        int p50 = queryTimes.get(p50Index);
        int p95 = queryTimes.get(p95Index);
        int p99 = queryTimes.get(p99Index);

        System.out.printf("Min (ms)       : %d\n", min);
        System.out.printf("Avg (ms)       : %.2f\n", avg);
        System.out.printf("Median/P50(ms) : %d\n", p50);
        System.out.printf("P95 (ms)       : %d\n", p95);
        System.out.printf("P99 (ms)       : %d\n", p99);
        System.out.printf("Max (ms)       : %d\n", max);
    }
}