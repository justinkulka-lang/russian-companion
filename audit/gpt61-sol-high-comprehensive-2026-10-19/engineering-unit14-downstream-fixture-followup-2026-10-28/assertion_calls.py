import re

def assertions(t):
 out=[]
 for m in re.finditer(r'\b(assert\w+|fail)\s*\(',t):
  start=m.start();i=m.end();depth=1;quote=None;escape=False
  while depth:
   c=t[i];i+=1
   if quote:
    if escape:escape=False
    elif c=='\\':escape=True
    elif c==quote:quote=None
   elif c in '\"\'':quote=c
   elif c=='(':depth+=1
   elif c==')':depth-=1
  out.append(re.sub(r'\s+',' ',t[start:i]))
 return out
