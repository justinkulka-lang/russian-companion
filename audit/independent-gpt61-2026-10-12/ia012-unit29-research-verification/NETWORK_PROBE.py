#!/usr/bin/env python3
"""Read-only source-access probe; run after an actual network-policy change."""
import json
import urllib.parse
import urllib.request
queries = [
    ('ru.wikipedia.org', {'action':'query','format':'json','list':'search','srsearch':'"причастный оборот" "местоимением"','srlimit':8}),
    ('en.wikibooks.org', {'action':'query','format':'json','titles':'Russian/Grammar/Participles'}),
]
for host, params in queries:
    url = 'https://' + host + '/w/api.php?' + urllib.parse.urlencode(params)
    try:
        with urllib.request.urlopen(url, timeout=25) as response:
            data = json.load(response)
            print(json.dumps({'host':host, 'status':response.status,'result':data}, ensure_ascii=False))
    except Exception as error:
        print(json.dumps({'host':host,'errorType':type(error).__name__,'error':str(error)}))
