import json,csv,sys
d=json.load(open(sys.argv[1]))
rows=list(csv.DictReader(open(sys.argv[2])))
steps={}; objs={}
for u in d['units']:
    for o in u['objectives']: objs[o['id']]=(u['number'],o)
    for s in u['sessions']:
        for st in s['steps']: steps[st['id']]=(u['number'],st)
items={i['id']:i for i in d['items']}; exs={e['id']:e for e in d['examples']}; refs={r['id']:r for r in d['references']}
for r in rows:
    cid=r['content_id']; print('#####',r['correction_id'],r['finding_id'],cid)
    if cid in steps:
        u,st=steps[cid]; print(' STEP',st['kind'],'|',st['title'],'|',st['body'][:900]); print('  ex:',st.get('exampleIds'))
    elif cid in items:
        i=items[cid]; print(' ITEM',i.get('kind'),'cue=',i.get('cue'),'| Q=',i['prompt'],'| opts=',i['options'],'| key=',i['answer']); print('  FB:',i['feedback'][:500])
        if i.get('distractorRationales'): print('  DR:',[x[:150] for x in i['distractorRationales']])
    elif cid in exs:
        e=exs[cid]; print(' EX',e.get('kind'),'|',e['display'],'||',e.get('note'))
    elif cid in objs:
        u,o=objs[cid]; print(' OBJ U',u,o['title'],'| kind',o['kind'],'gating',o.get('gating', o.get('required')),'| checks',o.get('checkItemIds'),'| instr',o.get('instructionStepIds'))
    elif cid in refs:
        x=refs[cid]; print(' REF',x['title'],'|',x['summary'][:300],'|',x['detail'][:900])
    else: print(' NOT FOUND')
