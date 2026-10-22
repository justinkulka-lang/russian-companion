import json,sys,os,hashlib,zipfile,csv,glob,collections
import xml.etree.ElementTree as ET
def sha(p): return hashlib.sha256(open(p,'rb').read()).hexdigest()
out={}
for name,zp,idx,ext in [
 ('broad_e5b5d3c0',sys.argv[1],sys.argv[2],sys.argv[3]),
 ('followup_20c5eb96',sys.argv[4],sys.argv[5],sys.argv[6])]:
    r={}
    ix=json.load(open(idx))
    r['archiveSha256']=sha(zp)
    r['archiveShaMatchesIndex']= r['archiveSha256']==(ix.get('sha256') or ix.get('archiveSha256'))
    z=zipfile.ZipFile(zp); r['zipTest']=z.testzip()
    names=set(z.namelist())
    mism=[]
    for m in ix['members']:
        p=os.path.join(ext,m['path'])
        if not os.path.isfile(p): mism.append(('missing',m['path'])); continue
        if sha(p)!=m['sha256'] or os.path.getsize(p)!=m['bytes']: mism.append(('diff',m['path']))
    r['indexMembers']=len(ix['members']); r['zipEntries']=len([n for n in names if not n.endswith('/')]); r['memberMismatches']=mism
    ctx=json.load(open(os.path.join(ext,'CONTEXT.json')))
    r['sourceCommit']=ctx['sourceCommit']; r['command']=' '.join(ctx['command']); r['exclusions']=ctx.get('exclusions'); r['selectors']=ctx.get('selectors')
    r['runExit']=open(os.path.join(ext,'RUN.exit')).read().strip()
    tot=collections.Counter(); fails=[]; skips=[]; classes=0
    for x in glob.glob(os.path.join(ext,'XML','*.xml')):
        t=ET.parse(x).getroot(); classes+=1
        for tc in t.iter('testcase'):
            tot['tests']+=1
            if tc.find('failure') is not None or tc.find('error') is not None: tot['failed']+=1; fails.append(tc.get('classname')+'#'+tc.get('name'))
            elif tc.find('skipped') is not None: tot['skipped']+=1; skips.append(tc.get('classname')+'#'+tc.get('name'))
            else: tot['passed']+=1
    r['xmlClasses']=classes; r['xmlCounts']=dict(tot); r['xmlFailures']=sorted(fails); r['xmlSkipped']=sorted(skips)
    res=json.load(open(os.path.join(ext,'RESULT.json')))
    r['resultJsonKeys']=list(res.keys())[:30]
    rows=list(csv.DictReader(open(os.path.join(ext,'RESULTS.csv'))))
    r['resultsCsvRows']=len(rows); r['resultsCsvCols']=list(rows[0].keys()) if rows else []
    out[name]=r
print(json.dumps(out,indent=1)[:6000])
json.dump(out,open(sys.argv[7],'w'),indent=1)
