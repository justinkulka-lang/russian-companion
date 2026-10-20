from pathlib import Path
import subprocess,json,hashlib
r=Path('/workspace/russian-companion');w=Path(__file__).parent;asset='app/src/main/assets/content/v2-curriculum.json'
refs={'beforePlural':'3812f0e793ac6478a809fe683b303048e6f1c69c','afterPlural':'4a67bce3a8c94efd7e63f49d911f91746376ecb2','afterAvailability':'fe919656bfa2775b5cc2c22f2496d4258343cfea','current':subprocess.check_output(['git','rev-parse','HEAD'],cwd=r,text=True).strip()}
raw={k:subprocess.check_output(['git','show',v+':'+asset],cwd=r) for k,v in refs.items()};packs={k:json.loads(v) for k,v in raw.items()};old=packs['beforePlural'];hist=packs['afterPlural'];current=packs['current'];availability=packs['afterAvailability']
unit=lambda p:next(x for x in p['units'] if x['number']==8)
unitItems=lambda p:[x for x in p['items'] if x['objectiveId'].startswith('obj-u8-')]
ids={'obj-u8-plural','obj-u8-spelling'};sessions={'u8-s-plural','u8-s-spelling'}
objs=lambda p:[x for x in unit(p)['objectives'] if x['id'] in ids]
sess=lambda p:[x for x in unit(p)['sessions'] if x['id'] in sessions]
exids={e for x in objs(hist) for e in x.get('exampleIds',[])}|{e for x in sess(hist) for s in x['steps'] for e in s.get('exampleIds',[])}
checks={'historicalUnit8ItemsRetained':unitItems(old)==unitItems(hist),'historicalExamplesRetained':all(x in hist['examples'] for x in old['examples']),'historicalProvenanceRetained':all(x in hist['provenanceRecords'] for x in old['provenanceRecords']),'historicalCapabilitiesExact':old['sourceCapabilities']==hist['sourceCapabilities'],'historicalAddedUnit8RecordsExactly21':sum(x['unitNumber']==8 for x in hist['provenanceRecords'])-sum(x['unitNumber']==8 for x in old['provenanceRecords'])==21,'currentPluralSpellingSessionsExact':sess(hist)==sess(current),'currentPluralSpellingObjectivesExact':objs(hist)==objs(current),'currentPluralSpellingItemsExact':[x for x in hist['items'] if x['objectiveId'] in ids]==[x for x in current['items'] if x['objectiveId'] in ids],'currentPluralSpellingExamplesExact':[x for x in hist['examples'] if x['id'] in exids]==[x for x in current['examples'] if x['id'] in exids],'currentPluralSpellingProvenanceExact':[x for x in hist['provenanceRecords'] if x['objectiveId'] in ids]==[x for x in current['provenanceRecords'] if x['objectiveId'] in ids],'currentAllUnit8ItemsEqualAvailabilitySource':unitItems(current)==unitItems(availability),'currentPriorExamplesRetained':all(x in current['examples'] for x in old['examples']),'currentPriorProvenanceRetained':all(x in current['provenanceRecords'] for x in old['provenanceRecords']),'currentCapabilitiesExact':old['sourceCapabilities']==current['sourceCapabilities']}
assert all(checks.values()),checks
hm={x['id']:x for x in unitItems(hist)};cm={x['id']:x for x in unitItems(current)};changed=[i for i in hm if hm[i]!=cm.get(i)]
assert set(changed)=={'u8-g-availability-0','u8-i-availability-1','u8-i-availability-2','u8-i-availability-3'}
(w/'EXACT_TARGET_COMPARISON.json').write_text(json.dumps(dict(commits=refs,assetSha256={k:hashlib.sha256(v).hexdigest() for k,v in raw.items()},comparisons=checks,changedUnit8ItemIds=changed,referencedExampleIds=sorted(exids)),indent=2)+'\n')
p=r/'app/src/test/java/dev/russiancompanion/v2/Unit8PluralInstructionTest.kt';s=p.read_text();start=s.index(' @Test fun unit8ScoredObjectsAndAllPriorNativeExamplesAndProvenanceRemainIdentical()');end=s.index('\n @Test',start+10);m=s[start:end]
m=m.replace('val p=pack();val old=baseline();','val p=historicalPack("4a67bce3a8c94efd7e63f49d911f91746376ecb2");val old=baseline();',1)
needle='  val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\\n"),v.ok)';assert needle in m
m=m.replace(needle,'''  // Retain the plural correction's exact historical contract above; bind current instruction below.
  val current=pack().also{V2PackIdentityIndex.warm(it)}
  val objectiveIds=setOf("obj-u8-plural","obj-u8-spelling")
  val sessionIds=setOf("u8-s-plural","u8-s-spelling")
  val historicalUnit=p.unit(8)!!;val currentUnit=current.unit(8)!!
  assertEquals(historicalUnit.objectives.filter{it.id in objectiveIds},currentUnit.objectives.filter{it.id in objectiveIds})
  assertEquals(historicalUnit.sessions.filter{it.id in sessionIds},currentUnit.sessions.filter{it.id in sessionIds})
  assertEquals(p.items.filter{it.objectiveId in objectiveIds},current.items.filter{it.objectiveId in objectiveIds})
  assertEquals(p.provenanceRecords.filter{it.objectiveId in objectiveIds},current.provenanceRecords.filter{it.objectiveId in objectiveIds})
  val exampleIds=(historicalUnit.objectives.filter{it.id in objectiveIds}.flatMap{it.exampleIds}+
   historicalUnit.sessions.filter{it.id in sessionIds}.flatMap{it.steps}.flatMap{it.exampleIds}).toSet()
  assertEquals(p.examples.filter{it.id in exampleIds},current.examples.filter{it.id in exampleIds})
  assertEquals(old.examples,current.examples.filter{e->old.examples.any{it.id==e.id}})
  assertEquals(old.sourceCapabilities,current.sourceCapabilities)
  val currentRecords=current.provenanceRecords.associateBy{it.itemId}
  old.provenanceRecords.forEach{assertEquals(it,currentRecords[it.itemId])}
  // Availability has a separately recorded correction; compare every Unit8 item to that exact source.
  val availabilitySource=historicalPack("fe919656bfa2775b5cc2c22f2496d4258343cfea")
  assertEquals(availabilitySource.items.filter{it.objectiveId.startsWith("obj-u8-")},current.items.filter{it.objectiveId.startsWith("obj-u8-")})
  val v=V2Validator.validate(current);assertTrue(v.errors.joinToString("\\n"),v.ok)''')
s=s[:start]+m+s[end:]
helper=''' private fun historicalPack(commit:String):V2CurriculumPack {
  val process=ProcessBuilder("git","show","$commit:app/src/main/assets/content/v2-curriculum.json")
   .directory(File(if(File("app").isDirectory)"." else "..")).start()
  val text=process.inputStream.bufferedReader().readText();assertEquals(0,process.waitFor())
  return V2PackLoader.parse(text).also{V2PackIdentityIndex.warm(it)}
 }
'''
s=s.replace(' private fun baseline():V2CurriculumPack',helper+' private fun baseline():V2CurriculumPack')
(w/'PROPOSED_TEST.kt').write_text(s)
print(json.dumps(checks,indent=2))
