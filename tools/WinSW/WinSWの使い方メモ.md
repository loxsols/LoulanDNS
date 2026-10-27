# WinSWの使い方メモ


## 基本的な流れ

1. WinSWのexeバイナリを適当なサービス名(HogeService.exe)にリネームする.
2. HogeService.exeと同じディレクトリに設定用のXMLファイル(HogeService.xml)を配置する.
3. XMLファイルの編集(後述)
4. 管理者モードでサービスのインストール : HogeService.exe install
5. 管理者モードでサービスを開始 : HogeService.exe start
6. 管理者モードでサービスを停止 : HogeService.exe stop
7. 管理者モードでサービスをアンインストール : HogeService.exe uninstall

## サービス定義XMLファイルの記述

以下のように記述する.

```
<service>
  <!-- 1. サービスの基本情報（システム内で一意にする） -->
  <id>LoulanDNSEndpointService</id>
  <name>LoulanDNS Endpoint Service</name>
  <description>LoulanDNS Endpoint Service</description>

  <!-- 2. 実行対象（cmd.exe を経由してbatをキックする） -->
  <executable>cmd.exe</executable>

  <!-- コマンド引数の設定 -->
  <argument>/c</argument>

  <!-- %BASE% はこのXML（WinSW）が置いてある絶対パスに自動置換される -->
  <argument>"%BASE%\..\..\..\bin\exec\exec-loulandns-endpoint-service.bat"</argument>

  <argument>-load</argument>
  <argument>database</argument>

  <argument>-su</argument>
  <argument>%SERVICE_USER_NAME%</argument>

  <argument>-sn</argument>
  <argument>%SERVICE_INSTANCE_NAME%</argument>

  <argument>-eu</argument>
  <argument>%ENDPOINT_USER_NAME%</argument>

  <argument>-en</argument>
  <argument>%ENDPOINT_INSTANCE_NAME%</argument>

  <!-- サービス個別の環境変数の定義 -->
  <!-- 起動batに渡したい引数の値をここで定義する -->

  <env name="SERVICE_USER_NAME" value="admin" />
  <env name="SERVICE_INSTANCE_NAME" value="test-service-subway" />


  <env name="ENDPOINT_USER_NAME" value="admin" />
  <env name="ENDPOINT_INSTANCE_NAME" value="test-endpoint-subway" />

  <!-- 4. ログ出力設定（オプション） -->
  <!-- javaの標準出力・エラーを logs フォルダに自動保存（日次ローテーション） -->
  <log mode="roll-by-time">
    <pattern>yyyyMMdd</pattern>
  </log>
</service>
```


