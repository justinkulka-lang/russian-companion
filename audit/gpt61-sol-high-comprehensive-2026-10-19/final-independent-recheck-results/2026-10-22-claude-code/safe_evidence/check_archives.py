import json,hashlib,zipfile,os,sys,collections
T=sys.argv[1]; cons=json.load(open(sys.argv[2])); idxp=sys.argv[3]
res=[];agg=collections.Counter()
for p,h in cons['original66ArchiveHashes'].items():
    fp=os.path.join(T,p)
    if not os.path.isfile(fp): res.append({'path':p,'present':False}); agg['missing']+=1; continue
    b=open(fp,'rb').read(); sh=hashlib.sha256(b).hexdigest()
    ok=sh==h; agg['sha_match']+=ok
    try:
        z=zipfile.ZipFile(fp); bad=z.testzip(); n=len(z.infolist()); agg['crc_ok']+= bad is None
    except Exception as e:
        bad='ERR:'+str(e); n=None
    res.append({'path':p,'present':True,'bytes':len(b),'sha256':sh,'expectedSha256':h,'shaMatch':ok,'zipEntries':n,'firstBadCrcMember':bad})
# 155 index
c155=cons['current155Files']; agg['current155']=len(c155)
chg=sum(1 for x in c155 if x.get('changed')); agg['current155_changed']=chg
mism=0
for x in c155:
    fp=os.path.join(T,x['path'])
    if os.path.isfile(fp):
        if hashlib.sha256(open(fp,'rb').read()).hexdigest()!=x['newSha256']: mism+=1
    else: mism+=1
agg['current155_newSha_mismatch_at_target']=mism
# historical prior-current zip
hz=zipfile.ZipFile(idxp); agg['historicalPriorCurrentZipEntries']=len(hz.infolist()); agg['historicalPriorCurrentZipCRC_ok']= hz.testzip() is None
old={x['oldSha256'] for x in c155 if x.get('changed')}
zsh={hashlib.sha256(hz.read(i)).hexdigest() for i in hz.infolist() if not i.is_dir()}
agg['changed_old_versions_present_in_zip']=len(old & zsh)
json.dump({'archives':res,'summary':dict(agg)},open(sys.argv[4],'w'),indent=1)
print(dict(agg))
