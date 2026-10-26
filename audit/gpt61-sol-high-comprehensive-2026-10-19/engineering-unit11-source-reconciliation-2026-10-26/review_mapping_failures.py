from pathlib import Path
import json,zipfile,hashlib,xml.etree.ElementTree as E
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-source-reconciliation-2026-10-26';W=Path(__file__).parent;sha=lambda b:hashlib.sha256(b).hexdigest();rows=json.loads((D/'ATTEMPT_3/FAILED_METHODS.json').read_text());ix=json.loads((D/'ATTEMPT_3/RAW_INDEX.json').read_text());out=[]
with zipfile.ZipFile(D/'ATTEMPT_3/RAW.zip') as z:
 for row in rows:
  n='REGRESSION/XML/TEST-'+row['testClass']+'.xml';b=z.read(n);assert len(b)==ix[n]['bytes'] and sha(b)==ix[n]['sha256'];suite=E.fromstring(b);case=next(t for t in suite.findall('testcase') if t.get('name')==row['method']);e=case.find('failure');m=e.get('message');assert sha(m.encode())==row['fullMessageSha256'] and len(m.encode())==row['fullMessageBytes'];a,c=m.split(' expected:<',1)[1].rsplit('> but was:<',1);c=c[:-1];x=dict(testClass=row['testClass'],method=row['method'],xmlMember=n,xmlSha256=sha(b),fullMessageBytes=len(m.encode()),fullMessageSha256=sha(m.encode()),sourceFrames=[s.strip() for s in e.text.splitlines() if row['testClass']+'.' in s],expectedBytes=len(a.encode()),actualBytes=len(c.encode()),expectedSha256=sha(a.encode()),actualSha256=sha(c.encode()))
  if len(a)>1000:
   old='unit11-prior-reading-possession-absence-v1';new='unit11-prior-noun-behavior-possession-absence-v2';assert a.count(old)==1 and a.replace(old,new)==c;x['exactDifference']=dict(expectedToken=old,actualToken=new,occurrences=1,everyOtherWholeUnitValueByteExact=True)
  else:
   assert '35de2337c00321867e4c30871b048181738017a79babc4f0135ad4131e1b6c69' in a and '1d4c25c25e20b7fb6629995fcfb66e29f02554e276c5a7f5ec69fb5ac0a2ca5c' in c;x.update(expectedValue=a,actualValue=c)
  out.append(x)
(W/'FULL_FAILURE_REVIEW.json').write_text(json.dumps(dict(actualBroadSource='3434fb8391a6bb4bee91c312a13bd79523db7799',originalCounts=dict(reported=1441,passed=1438,failed=2,errors=0,skipped=1),classes=249,exitCode=1,bothCompleteXmlMessagesReadAndHashMatched=True,failures=out),indent=2)+'\n');print(json.dumps(out,indent=2))
