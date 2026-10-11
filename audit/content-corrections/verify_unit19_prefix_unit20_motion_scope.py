#!/usr/bin/env python3
"""Compare against an exact baseline; normalize only typed provenance defaults."""
import json,sys
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
b=json.loads(Path(sys.argv[1]).read_text());p=json.loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_text());ledger=json.loads((ROOT/'audit/content-corrections/UNIT19_PREFIX_UNIT20_MOTION_CORRECTION.json').read_text())
def norm(x):return dict(reviewerIdentity='',reviewerQualification='',reviewDate='',reviewedDigest='',audioAssetId=None,**{k:v for k,v in x.items() if k not in ('reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest','audioAssetId')}) | {k:v for k,v in x.items() if k in ('reviewerIdentity','reviewerQualification','reviewDate','reviewedDigest','audioAssetId')}
old={x['itemId']:x for x in b['provenanceRecords']};new={x['itemId']:x for x in p['provenanceRecords']};revised=set(ledger['revisedResearchProvenanceIds'])
assert set(new)-set(old)==set(ledger['addedProvenanceIds']);assert set(old)<=set(new)
for id,o in old.items():
 n=norm(new[id]);o=norm(o)
 if id in revised:
  assert n['originalSourceText'].startswith(o['originalSourceText']);n['originalSourceText']=o['originalSourceText'];n['sourceHash']=o['sourceHash']
 assert n==o,id
for u in b['units']:
 if u['number'] not in (19,20):assert u==next(x for x in p['units'] if x['number']==u['number'])
for k in b:
 if k not in ('units','items','provenanceRecords','sourceCapabilities'):assert b[k]==p[k],k
for i in b['items']:
 if i['id'] not in ledger['changedItemIds']:assert i==next(x for x in p['items'] if x['id']==i['id'])
for cap in b['sourceCapabilities']:
 n=next(x for x in p['sourceCapabilities'] if x['id']==cap['id'])
 if cap['id'] in ('cap-u20-motion-po-setting-out','cap-u20-motion-prefixed-path'):
  n=dict(n);assert n['exactSpan'].startswith(cap['exactSpan']);n['exactSpan']=cap['exactSpan'];n['sourceHash']=cap['sourceHash']
 assert n==cap,cap['id']
print('PASS: exact scope; all licensed original source text and rights retained; two pending motion research spans only append new instruction; all other units/items/capabilities and pack governance preserved modulo typed schema defaults.')
