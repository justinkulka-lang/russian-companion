import json,hashlib,re,sys,collections
d=json.load(open(sys.argv[1]))
pool=d['sourceTextPool']
poolvals=list(pool.values()) if isinstance(pool,dict) else pool
pooltext=[v if isinstance(v,str) else json.dumps(v,ensure_ascii=False) for v in poolvals]
objs={o['id']:(u['number'],o) for u in d['units'] for o in u['objectives']}
srcs={s['id'] for s in d['sources']}
out=[]
for c in d['sourceCapabilities']:
    span=c['exactSpan']
    h=hashlib.sha256(span.encode()).hexdigest()
    hm = h==c['sourceHash']
    inpool=any(span in t for t in pooltext)
    on=objs.get(c['objectiveId'])
    unit_ok = on is not None and on[0]==c['unitNumber']
    flags=[]
    if 'http' in span: flags.append('URL')
    if '[[' in span or '{{' in span or "'''" in span or '\n!' in span: flags.append('WIKIMARKUP')
    if c['sourceId'] not in srcs: flags.append('NOSRC')
    gating = on[1].get('gating') if on else None
    out.append((c['id'],c['sourceId'],c['sourceRevision'],c['unitNumber'],c['objectiveId'],gating,c['supportedClaim'],hm,inpool,unit_ok,flags,len(span),span[:260].replace('\n',' / ')))
for o in out: print(' | '.join(str(x) for x in o))
print('hash_match',sum(o[7] for o in out),'inpool',sum(o[8] for o in out),'unit_ok',sum(o[9] for o in out))
print(collections.Counter(o[1] for o in out))
