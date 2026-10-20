from pathlib import Path
import subprocess,json
here=Path('/workspace/artifacts/post-correction-broad-2026-10-20/continuation')
r=Path('/workspace/russian-companion')
run=here/'MISSING_AND_INTERRUPTED'
result=subprocess.run(['bash',str(run/'run.sh')],cwd=r)
collection=subprocess.run(['python',str(here/'collect_run.py'),'MISSING_AND_INTERRUPTED'],cwd=r)
(run/'LAUNCHER_RESULT.json').write_text(json.dumps(dict(gradleExit=result.returncode,collectorExit=collection.returncode))+'\n')
