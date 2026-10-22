import json,sys
d=json.load(open(sys.argv[1]))
out=sys.argv[2]
with open(out,'w') as f:
    for k in ['linguisticReview','admissions']:
        f.write(f'===== {k}\n'+json.dumps(d[k],ensure_ascii=False,indent=1)+'\n')
    f.write('===== sources\n')
    rights={r.get('sourceId',r.get('id')):r for r in d['rights']}
    for s in d['sources']:
        f.write(json.dumps(s,ensure_ascii=False)+'\n')
    f.write('===== rights\n')
    for r in d['rights']:
        f.write(json.dumps(r,ensure_ascii=False)+'\n')
    f.write('===== references\n')
    for r in d['references']:
        f.write(json.dumps(r,ensure_ascii=False)+'\n')
    f.write('===== letters\n')
    for r in d['letters']:
        f.write(json.dumps(r,ensure_ascii=False)+'\n')
    f.write('===== unitReviews\n')
    for r in d['unitReviews']:
        f.write(json.dumps(r,ensure_ascii=False)+'\n')
