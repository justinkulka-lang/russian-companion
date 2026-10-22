"""Compact view of a rendered unit: full TEACH/GUIDED/CHECK bodies; remediation bodies truncated; items without why/contrast."""
import sys, re
lines = open(sys.argv[1]).read().split('\n')
out = []; in_rem = False
for ln in lines:
    if ln.startswith('  [') and ' REMEDIATE ' in ln: in_rem = True; out.append(ln); continue
    if ln.startswith('  [') or ln.startswith('##') or ln.startswith('---'): in_rem = False
    if re.match(r'^       (why|distractorDifference|contrast):', ln): continue
    if 'OWNED EDITION MAPPING' in ln or ln.startswith('    bookDirection: Owned-book'): continue
    if in_rem:
        if ln.startswith('    BODY'): out.append(ln[:220] + (' …' if len(ln) > 220 else '')); continue
        if ln.startswith('          '): continue
        if ln.startswith('    EX '): continue
    out.append(ln)
print('\n'.join(out))
