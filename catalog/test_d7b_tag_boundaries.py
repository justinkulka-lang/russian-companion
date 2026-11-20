"""T16 (owner decision D27a): apply_d7b_selection_records.BLOCK matches complete tag names only.

Run: python3 -I -m unittest catalog/test_d7b_tag_boundaries.py (from the repository root) or python3 -I catalog/test_d7b_tag_boundaries.py.
"""
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import apply_d7b_selection_records as d7b  # noqa: E402


def joined(line: str, word: str) -> bool:
    return d7b.two_part(d7b.displayed(line), word)


class TagBoundaryTest(unittest.TestCase):
    def test_prefixed_names_with_colon_or_underscore_are_boundaries(self):
        for tag in ("span:probe", "span_probe", "b:probe", "b_probe", "em:x", "a_b"):
            with self.subTest(tag=tag):
                self.assertFalse(joined(f"<{tag}>Иван</{tag}>ович", "Иванович"))
                self.assertFalse(joined(f'<{tag} class="x">Иван</{tag}>ович', "Иванович"))
                self.assertFalse(joined(f"Иван<{tag}/>ович", "Иванович"))

    def test_longer_names_and_unknown_elements_stay_boundaries(self):
        for tag in ("spanner", "bdox", "center", "xqzblock", "div"):
            with self.subTest(tag=tag):
                self.assertFalse(joined(f"<{tag}>Иван</{tag}>ович", "Иванович"))

    def test_complete_inline_names_still_join(self):
        for tag in d7b.INLINE_TAGS:
            with self.subTest(tag=tag):
                self.assertTrue(joined(f"<{tag}>Иван</{tag}>ович", "Иванович"))
        self.assertTrue(joined('<span class="x">Иван</span>ович', "Иванович"))
        self.assertTrue(joined("<b>Иван</b >ович", "Иванович"))
        self.assertTrue(joined("Иван<wbr/>ович", "Иванович"))


if __name__ == "__main__":
    unittest.main()
