import json,hashlib,csv,sys,os,subprocess,collections
T=sys.argv[1]; paths=[r['path'] for r in csv.DictReader(open(sys.argv[2]))]
cur=json.load(open(os.path.join(T,'app/src/main/assets/content/v2-curriculum.json')))
items={i['id']:i for i in cur['items']}
kt=open(os.path.join(T,'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt')).read()
import re
policy_ids=set(re.findall(r'CumulativeReviewPolicy\("([a-z0-9-]+)"',kt))|set(re.findall(r'const val id = "([a-z0-9-]+)"',kt))
def ct(v): return f"{len(v.encode())}:{v}"
def cp(parts): return f"{len(parts)}{{"+";".join(ct(p) for p in parts)+"}"
def h(s): return hashlib.sha256(s.encode()).hexdigest()
def adig(a):
    parts=[str(a['schema']),a['id'],str(a['targetUnit']),a['policyId'],a['policyDigest'],a['responseRuleDigest'],str(a['boundary'])]
    parts+=[cp([s['objectiveId'],s['skill'],s['itemId'],str(s['ownerUnit']),s['itemDigest'],s['unitDigest'],s['teachingDigest']]) for s in a['slots']]
    return h(cp(parts))
out=[]
agg=collections.Counter()
for p in paths:
    fp=os.path.join(T,p)
    if not os.path.isfile(fp): out.append({'path':p,'present':False}); agg['missing']+=1; continue
    b=open(fp,'rb').read(); x=json.loads(b)
    blob=subprocess.run(['git','-C',T,'hash-object',fp],capture_output=True,text=True).stdout.strip()
    defects=[]
    A=x.get('reviewAssignments',[]); R=x.get('reviewAnswers',[])
    ids=[a['id'] for a in A]
    if len(set(ids))!=len(ids): defects.append('dup-assignment')
    if len({r['id'] for r in R})!=len(R): defects.append('dup-answer')
    if len({r['acceptedOrder'] for r in R})!=len(R): defects.append('dup-order')
    mx=max([r['acceptedOrder'] for r in R]+[0])
    am={a['id']:a for a in A}
    for a in A:
        if adig(a)!=a['digest']: defects.append('assignment-digest')
        if a['boundary']<0 or a['boundary']>mx: defects.append('boundary')
        if not (2<=len(a['slots'])<=6): defects.append('slot-count')
        if a['policyId'] not in policy_ids: defects.append('policy-not-in-current-registry:'+a['policyId'])
        for s in a['slots']:
            if s['itemId'] not in items: defects.append('slot-item-missing-now:'+s['itemId'])
            if not (1<=s['ownerUnit']<a['targetUnit']): defects.append('owner-unit')
    for r in R:
        a=am.get(r['assignmentId'])
        if a is None or r['assignmentDigest']!=a['digest'] or not (0<=r['slotIndex']<len(a['slots'])) or r['acceptedOrder']<=a['boundary'] or r.get('role')!='CUMULATIVE_REVIEW' or r.get('schema')!=1: defects.append('answer-link')
    recs=x.get('recordings',[])
    keymatch=[]
    for a in A:
        for i,s in enumerate(a['slots']):
            ans=[r for r in R if r['assignmentId']==a['id'] and r['slotIndex']==i]
            if ans:
                last=max(ans,key=lambda r:r['acceptedOrder'])
                keymatch.append(last['selected']==items.get(s['itemId'],{}).get('answer'))
    uc=x.get('unitCompletions',[])
    row={'path':p,'present':True,'bytes':len(b),'sha256':hashlib.sha256(b).hexdigest(),'git_blob':blob,'schemaVersion':x.get('schemaVersion'),
         'assignments':len(A),'assignment_schemas':sorted({a['schema'] for a in A}),'answers':len(R),'unitCompletions':len(uc),
         'completions_preReviewActivation':sum(1 for c in uc if c.get('preReviewActivation')),'completions_with_assignment':sum(1 for c in uc if c.get('reviewAssignmentId')),
         'recordings':len(recs),'latest_answers_matching_current_key':f"{sum(keymatch)}/{len(keymatch)}",'defects':sorted(set(defects))}
    out.append(row); agg['present']+=1; agg['recordings']+=len(recs); agg['with_defects']+=bool(defects)
json.dump(out,open(sys.argv[3],'w'),indent=1,ensure_ascii=False)
print(dict(agg)); print('policy ids in registry',len(policy_ids))
print(collections.Counter(tuple(r.get('defects',['MISSING'])) for r in out).most_common(10))
print(collections.Counter(r.get('schemaVersion') for r in out), collections.Counter(tuple(r.get('assignment_schemas',[])) for r in out))
print(collections.Counter(r.get('latest_answers_matching_current_key') for r in out))
