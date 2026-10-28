from pathlib import Path
import shutil,ast,json
W=Path('/workspace/artifacts/engineering-unit14-downstream-fixture-followup-2026-10-28')
D=Path('/workspace/russian-companion/audit/gpt61-sol-high-comprehensive-2026-10-19')/W.name
OLD=Path('/workspace/artifacts/unit14-historical-comparison-followup-2026-10-28')
for name in ['run.py','resources.gradle','launch.py','supervise.py','durable_monitor.py','start_runtime.py']:
    text=(OLD/name).read_text()
    if name=='run.py':text=text.replace('79 methods / 13 full classes: seven failure classes and six unchanged Unit13/14 baseline/UI/compatibility/binding controls; preserve all failures; not complete suite','119 methods / 17 full classes:60 original broad failures,45 siblings,14 unchanged Unit14/15 controls; preserve every outcome; not complete suite')
    (W/name).write_text(text)
for name in ['evidence.py','verify_results.py','record_discovery.py','completion_worker.py']:
    shutil.copyfile(Path('/workspace/artifacts/unit14-downstream-tool-sources')/name,W/name)
shutil.copyfile(__file__,W/'configure.py')
for p in W.glob('*.py'):ast.parse(p.read_text())
for p in W.iterdir():
    if p.is_file()and p.suffix in ['.py','.gradle']:shutil.copyfile(p,D/p.name)
print(json.dumps({'toolingPrepared':True,'directory':str(D),'workspace':str(W)}))
