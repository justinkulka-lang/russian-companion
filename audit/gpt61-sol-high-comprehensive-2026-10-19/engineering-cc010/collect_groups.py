"""Reconcile per-group diagnostics; 24 observations are not 24 JUnit tests."""
from pathlib import Path
import json, sys, xml.etree.ElementTree as E
root = Path(sys.argv[1])
rows = []
for path in (root/'VERIFIED/XML').glob('TEST-*.xml'):
    for output in E.parse(path).getroot().findall('system-out'):
        for line in (output.text or '').splitlines():
            if line.startswith('CC010_GROUP '): rows.append(dict(part.split('=',1) for part in line.split()[1:]))
assert len(rows) == 24
assert {(int(r['group']),r['kind']) for r in rows} == {(n,k) for n in range(1,9) for k in ['COUNTER','CREDIT','BACKUP']}
for row in rows:
    if row['kind']=='CREDIT': assert row['noLeak']=='true' and row['otherWorkRequired']=='true'
    if row['kind']=='BACKUP': assert row['wrongRowsPreserved']=='true' and row['resumeCurrent']=='true'
    if row['kind']=='COUNTER': assert int(row['value']) in [1,2]
result = dict(observations=rows,groupCount=8,observationCount=24,areAdditionalJUnitMethods=False,
    counterValues={r['group']:int(r['value']) for r in rows if r['kind']=='COUNTER'},
    note='Counter values are observed existing policy behavior, not assertions of semantic novelty or a new rule.')
(root/'GROUP_RUNTIME_OBSERVATIONS.json').write_text(json.dumps(result,indent=2)+'\n')
print('Eight groups have complete counter/credit/backup observations;counter values',result['counterValues'])
