# Received bytes and whitespace verification

The default Git staged whitespace check flags CRLF line endings in preserved original attachments/payloads and an original PR-body ending. These received bytes were deliberately not normalized: their SHA256 identity is the evidence. All copied receipt files remain byte-identical to their outside-checkout originals. The generated documentation and three retained-content pointers receive a separate ordinary whitespace check. No test assertion, runtime input or verification guard is changed by this handling.
