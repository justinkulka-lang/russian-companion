from pathlib import Path
import json,hashlib,zipfile,xml.etree.ElementTree as ET,re
R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit11-source-reconciliation-2026-10-26';W=Path(__file__).parent
sha=lambda b:hashlib.sha256(b).hexdigest();rows=json.loads((D/'ATTEMPT_1/FAILED_METHODS.json').read_text());ix=json.loads((D/'ATTEMPT_1/RAW_INDEX.json').read_text());old='unit11-prior-reading-possession-absence-v1';new='unit11-prior-noun-behavior-possession-absence-v2';out=[]
with zipfile.ZipFile(D/'ATTEMPT_1/RAW.zip') as z:
 for row in rows:
  n='REGRESSION/XML/TEST-'+row['testClass']+'.xml';b=z.read(n);assert sha(b)==ix[n]['sha256'] and len(b)==ix[n]['bytes'];suite=ET.fromstring(b);case=next(t for t in suite.findall('testcase') if t.get('name')==row['method']);e=case.find('failure');m=e.get('message','');assert sha(m.encode())==row['fullMessageSha256'] and len(m.encode())==row['fullMessageBytes'];frames=[x.strip() for x in e.text.splitlines() if row['testClass']+'.' in x];x=dict(testClass=row['testClass'],method=row['method'],xmlMember=n,xmlSha256=sha(b),messageBytes=len(m.encode()),messageSha256=sha(m.encode()),sourceFrames=frames[:4])
  if ' expected:<' in m and '> but was:<' in m and m.endswith('>'):
   expected,actual=m.split(' expected:<',1)[1].rsplit('> but was:<',1);actual=actual[:-1];x.update(expectedValueBytes=len(expected.encode()),actualValueBytes=len(actual.encode()),expectedSha256=sha(expected.encode()),actualSha256=sha(actual.encode()))
   if max(len(expected),len(actual))>1000:
    directions=[(old,new),(new,old)];proof=[]
    for a,b in directions:
     if expected.count(a)==1 and expected.replace(a,b)==actual:proof.append(dict(expectedToken=a,actualToken=b,occurrences=1,everyOtherValueByteExact=True))
    assert len(proof)==1,(row['method'],'Not exactly one declaration difference');x['exactWholeValueDifference']=proof[0]
   else:x.update(expectedValue=expected,actualValue=actual)
  else:x['fullBoundedMessage']=m
  out.append(x)
(W/'FULL_FAILURE_REVIEW.json').write_text(json.dumps(dict(actualSource='e31158998d81817f7bbcf44a7c2808aba5a8f917',originalScope='395reported/386passed/8failed/0errors/1physicalskip,71classes,exit1',allEightCompleteXmlMessagesReadAndHashMatched=True,failures=out),indent=2)+'\n')
for x in out:print(json.dumps({k:v for k,v in x.items() if k not in ['xmlMember','xmlSha256','expectedSha256','actualSha256','messageSha256']}))
