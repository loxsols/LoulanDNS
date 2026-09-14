package org.loxsols.net.service.dns.loulandns.client.command.statistics.perf;


import java.io.IOException;
import java.time.Duration;


/**
 * UNIXのtimeコマンド互換I/Fの時間計測コマンド.
 * 
 * 
 * TimeMeasureCommand
 */
public class TimeMeasureCommand
{
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.err.println("Usage: <command> [args...]");
            System.exit(1);
        }

        // コマンドライン引数をそのまま外部プロセスに渡す
        ProcessBuilder pb = new ProcessBuilder(args);
        
        // 子プロセスの標準入出力を親プロセスと共有（実行中の画面表示を維持）
        pb.inheritIO();

        long startTime = System.nanoTime();

        try
        {
            Process process = pb.start();
            ProcessHandle handle = process.toHandle();

            // プロセスの終了を待機
            int exitCode = process.waitFor();

            long realNanos = System.nanoTime() - startTime;

            // プロセスのCPU時間（ユーザー時間+システム時間）を取得
            Duration cpuDuration = handle.info()
                    .totalCpuDuration()
                    .orElse(Duration.ZERO);

            // 本家 time コマンドと同様に stderr に出力
            System.err.println();
            System.err.printf("real    %s%n", formatDuration(Duration.ofNanos(realNanos)));
            System.err.printf("cpu     %s%n", formatDuration(cpuDuration));

            // 子プロセスの終了コードをそのまま引き継ぐ
            System.exit(exitCode);

        }
        catch (IOException e)
        {
            System.err.println("Execution failed: " + e.getMessage());
            System.exit(1);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    private static String formatDuration(Duration duration)
    {
        long minutes = duration.toMinutes();
        int seconds = duration.toSecondsPart();
        int millis = duration.toMillisPart();
        return String.format("%dm%d.%03ds", minutes, seconds, millis);
    }
}