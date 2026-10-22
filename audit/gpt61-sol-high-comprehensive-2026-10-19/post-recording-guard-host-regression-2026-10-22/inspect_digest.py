from pathlib import Path
import json,re,subprocess,hashlib,struct
r=Path('/workspace/russian-companion');w=Path(__file__).parent
k=(r/'app/src/main/java/dev/russiancompanion/release/ReleaseSourceDigest.kt').read_text();roots=re.findall(r'"([^"]+)"',k.split('private val includedRoots = listOf(',1)[1].split(')',1)[0]);source='e5b5d3c08689cc04eadcb4c2edd155940dee7bf7';prior='ff2a5f5535e2bb57c47fadeff74989db50508d90'
def digest(commit):
 paths=subprocess.check_output(['git','ls-tree','-r','--name-only',commit,'--',*roots],cwd=r,text=True).splitlines();h=hashlib.sha256();files={}
 for p in sorted(paths):
  if p.startswith(('release/evidence/','release/acceptance/forms/','catalog/audio-cache/','__pycache__/')) or p in ['release/retained-evidence.json','release/acceptance/candidate.json'] or '/__pycache__/' in p or p.endswith(('.pyc','.DS_Store')):continue
  b=subprocess.check_output(['git','show',commit+':'+p],cwd=r);n=p.encode();h.update(struct.pack('>I',len(n)));h.update(n);h.update(struct.pack('>I',len(b)));h.update(b);files[p]=hashlib.sha256(b).hexdigest()
 return h.hexdigest(),files
before,old=digest(prior);now,new=digest(source);candidate=json.loads((r/'release/acceptance/candidate.json').read_text());changes=[dict(path=p,beforeSha256=old.get(p),currentSha256=new.get(p)) for p in sorted(set(old)|set(new)) if old.get(p)!=new.get(p)]
v=dict(inspection='READ_ONLY_NO_CORRECTION',previousSource=prior,currentSource=source,algorithm='Kotlin ReleaseSourceDigest included roots/exclusions;sorted UTF8 paths,4byte BE path and content lengths,SHA256',previousComputedDigest=before,currentComputedDigest=now,candidateStoredDigest=candidate['sourceDigest'],candidateMatchesPrior=before==candidate['sourceDigest'],candidateMatchesCurrent=now==candidate['sourceDigest'],releaseSourceInputDelta=changes,independentAudit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',candidateEdited=False)
(w/'DIGEST_READ_ONLY_DIAGNOSIS.json').write_text(json.dumps(v,indent=2)+'\n');print(json.dumps(v,indent=2))
