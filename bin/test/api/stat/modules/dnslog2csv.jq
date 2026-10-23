# ヘッダー行の定義出力はスクリプト外で実施するため、ここではコメントアウト（または削除）
# (["loggingDateTime", "userName", "dnsserviceInstanceName", "qname", "qtype", "qclass", "query-id", "dns-response"] | @csv),

# NDJSONのデータを1行ずつ直接受けて処理する
. as $log |
select($log | type == "object") |
$log.commonSystemLogID as $logId |
(
  ($log.commonSystemLogPropertiesInfoList // [])
  | map(select(.commonSystemLogID == $logId))
) as $properties |
(
  $properties
  | map({
      (.commonSystemLogPropertyKey): .commonSystemLogPropertyValue
    })
  | add // {}
) as $propertyValues |
(
  $properties
  | map(.commonSystemLogPropertyKey)
  | map(select(test("^dns\\.message\\.response\\.rr-index:[0-9]+$")))

  | map(capture(":(?<index>[0-9]+)$").index | tonumber)
  | unique
  | sort
) as $responseIndexes |
$properties | to_entries[] |
select(
  .value.commonSystemLogPropertyKey
    == "dns.message.question.qname"
) |
.key as $index |
[
  $log.loggingDateTime,
  $log.userName,
  $log.dnsserviceInstanceName,
  $properties[$index].commonSystemLogPropertyValue,
  $properties[$index + 1].commonSystemLogPropertyValue,
  $properties[$index + 2].commonSystemLogPropertyValue,
  (if $properties[$index - 1].commonSystemLogPropertyKey
    == "dns.message.question.query-id"
   then $properties[$index - 1].commonSystemLogPropertyValue
   else null
   end),
  (
    $responseIndexes
    | map(
        . as $rrIndex |
        [
          ($propertyValues["dns.message.response.rname:\($rrIndex)"] // ""),
          ($propertyValues["dns.message.response.rtype:\($rrIndex)"] // ""),
          ($propertyValues["dns.message.response.rclass:\($rrIndex)"] // "")
        ]
        | join(":")
      )

    | join(" | ")
  ),
  $propertyValues["stat.dns.service.process.start.time"],
  $propertyValues["stat.dns.service.process.end.time"],
  $propertyValues["stat.dns.service.process.time.msec"]
] | @csv