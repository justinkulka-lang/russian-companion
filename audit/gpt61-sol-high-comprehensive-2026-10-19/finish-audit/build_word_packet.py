#!/usr/bin/env python3
"""Package complete pinned audit inputs in a native Word file with verified embedded parts."""
from pathlib import Path
import csv,hashlib,io,json,subprocess,sys,tempfile,xml.etree.ElementTree as ET,zipfile
from docx import Document
ROOT=Path(__file__).resolve().parents[3]
BASE=Path(__file__).resolve().parent
ART=Path('/workspace/artifacts')
RESULTS=BASE.parent/'continuation-results/2026-10-19-199b3229'
sha=lambda b:hashlib.sha256(b).hexdigest()
def main():
    ART.mkdir(parents=True,exist_ok=True)
    snapshot=ART/'GPT61_Sol_High_Comprehensive_Audit_5c176eb2.zip'
    audit=RESULTS/'GPT61_SAME_CHAT_AUDIT_CONTINUATION_RESULTS.zip'
    assert snapshot.stat().st_size==74854067 and sha(snapshot.read_bytes())=='8326a6e03bffe8e30a3479d0e469e37822f61ea88a1e58a5419f493a4476c15d'
    assert audit.stat().st_size==601558 and sha(audit.read_bytes())=='199b3229665b769b043434f1d6a69abb242d91b16fd6ed26187fad1acb294185'
    parts=[('pinnedRepositorySnapshot','word/embeddings/PINNED_REPOSITORY_SNAPSHOT.zip',snapshot),('currentSixteenOutputAudit','word/embeddings/CURRENT_SIXTEEN_OUTPUT_AUDIT.zip',audit),('finishPrompt','word/embeddings/FINISH_AUDIT_PROMPT.txt',BASE/'FINISH_AUDIT_PROMPT.txt'),('returnSpecification','word/embeddings/REQUIRED_RETURNS.md',BASE/'REQUIRED_RETURNS.md'),('gapPlan','word/embeddings/GAP_COMPLETION_PLAN.md',BASE/'GAP_COMPLETION_PLAN.md'),('openingHelper','word/embeddings/OPEN_AUDIT_INPUTS.py',BASE/'OPEN_AUDIT_INPUTS.py')]
    manifest={'schemaVersion':1,'purpose':'Complete repository inputs for the SAME independent ChatGPT audit','target':'5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed','targetTree':'f95bfa3c1ae6b8ac07b42b6db9fa69c5e247830f','newRuntimeExecution':False,'newFixes':False,'privatePdfIncluded':False,'files':[{'role':role,'docxPart':name,'bytes':path.stat().st_size,'sha256':sha(path.read_bytes())} for role,name,path in parts]}
    doc=Document();doc.add_heading('Russian Companion — complete audit inputs',0)
    doc.add_paragraph('Upload this original Word file directly into the SAME ChatGPT audit conversation. It contains the complete pinned repository snapshot and the latest sixteen-output audit as embedded archives, not only instructions. Use Python/file tools to open those embedded parts. The private Penguin PDF remains a separate attachment. No new fixes, Android execution or approvals are supplied.')
    doc.add_heading('Opening instructions for ChatGPT',1)
    doc.add_paragraph('Open this DOCX using Python zipfile. Read word/embeddings/INPUT_MANIFEST.json. Inspect and extract word/embeddings/OPEN_AUDIT_INPUTS.py, then run it with this DOCX path and a fresh output directory. The helper verifies embedded hashes, safely extracts inputs, verifies the authentic target snapshot and latest result hashes, and writes OPENED_INPUTS.json. If Python is unavailable, use authorized repository access and explicitly retain unavailable scope. Ordinary Word text extraction does not expose the embedded archives.')
    doc.add_heading('Copy and paste into the SAME ChatGPT audit',1)
    for line in (BASE/'FINISH_AUDIT_PROMPT.txt').read_text().splitlines():doc.add_paragraph(line)
    doc.add_heading('Required return deliverables',1)
    for line in (BASE/'REQUIRED_RETURNS.md').read_text().splitlines():doc.add_paragraph(line)
    doc.add_heading('Remaining scope and ownership',1)
    for line in (BASE/'GAP_COMPLETION_PLAN.md').read_text().splitlines():doc.add_paragraph(line)
    doc.add_heading('Embedded input identities',1)
    for row in manifest['files']:doc.add_paragraph(f"{row['docxPart']}\n{row['bytes']} bytes\nSHA256 {row['sha256']}")
    small=ART/'Russian_Companion_Finish_Audit_Instructions.docx';doc.save(small)
    with zipfile.ZipFile(small) as z:entries={name:z.read(name) for name in z.namelist()}
    ct_ns='http://schemas.openxmlformats.org/package/2006/content-types'
    ct=ET.fromstring(entries['[Content_Types].xml']);exts={e.attrib.get('Extension') for e in ct}
    for ext,mime in [('zip','application/zip'),('json','application/json'),('txt','text/plain'),('md','text/plain'),('py','text/plain')]:
        if ext not in exts:ET.SubElement(ct,'{'+ct_ns+'}Default',Extension=ext,ContentType=mime)
    entries['[Content_Types].xml']=ET.tostring(ct,encoding='utf-8',xml_declaration=True)
    rel_ns='http://schemas.openxmlformats.org/package/2006/relationships';rel=ET.fromstring(entries['word/_rels/document.xml.rels'])
    for i,(_,name,_) in enumerate(parts,1):ET.SubElement(rel,'{'+rel_ns+'}Relationship',Id='auditInput'+str(i),Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/package',Target=name.removeprefix('word/'))
    ET.SubElement(rel,'{'+rel_ns+'}Relationship',Id='auditInputManifest',Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/package',Target='embeddings/INPUT_MANIFEST.json')
    entries['word/_rels/document.xml.rels']=ET.tostring(rel,encoding='utf-8',xml_declaration=True)
    output=ART/'Russian_Companion_Full_Audit_Inputs.docx'
    with zipfile.ZipFile(output,'w') as z:
        for name,data in entries.items():z.writestr(name,data,compress_type=zipfile.ZIP_DEFLATED)
        for _,name,path in parts:z.write(path,name,compress_type=zipfile.ZIP_STORED if name.endswith('.zip') else zipfile.ZIP_DEFLATED)
        z.writestr('word/embeddings/INPUT_MANIFEST.json',json.dumps(manifest,indent=2)+'\n',compress_type=zipfile.ZIP_DEFLATED)
    with zipfile.ZipFile(output) as z:
        assert z.testzip() is None
        for row in manifest['files']:
            b=z.read(row['docxPart']);assert len(b)==row['bytes'] and sha(b)==row['sha256']
    # Validate it as a Word document in addition to checking the container.
    reopened=Document(output);assert reopened.paragraphs[0].text.startswith('Russian Companion')
    with tempfile.TemporaryDirectory(dir=ART,prefix='verify-full-audit-word-') as temp:
        run=subprocess.run([sys.executable,str(BASE/'OPEN_AUDIT_INPUTS.py'),str(output),str(Path(temp)/'opened')],capture_output=True,text=True)
        (ART/'full-audit-word-verification.log').write_text(run.stdout+run.stderr)
        assert run.returncode==0,run.stderr
    prompt=ART/'Russian_Companion_Finish_Audit_Prompt.txt';prompt.write_bytes((BASE/'FINISH_AUDIT_PROMPT.txt').read_bytes())
    delivery={'target':manifest['target'],'fullWordInputs':{'file':output.name,'bytes':output.stat().st_size,'sha256':sha(output.read_bytes())},'smallWordInstructions':{'file':small.name,'bytes':small.stat().st_size,'sha256':sha(small.read_bytes())},'promptTextFile':prompt.name,'all6427TargetFilesIncluded':True,'allCurrentSixteenOutputsAndSafeEvidenceIncluded':True,'embeddedPartsAndCrcVerified':True,'sourceGitCommitTreeAndAllBlobProofVerifierExecutedSuccessfully':True,'allCurrentAuditInternalHashesVerified':True,'validWordDocumentReopened':True,'sourceChanges':False,'runtimeTestsExecuted':0,'auditCompletedByPackaging':False,'privatePdfIncluded':False,'physicalAcceptance':'OUTSTANDING_LAST'}
    (BASE/'DELIVERY.json').write_text(json.dumps(delivery,indent=2)+'\n');(BASE/'INPUT_MANIFEST.json').write_text(json.dumps(manifest,indent=2)+'\n');print(json.dumps(delivery))
if __name__=='__main__':main()
