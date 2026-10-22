#!/usr/bin/env python3
"""kustomization.yaml の images から、指定したイメージの digest だけを書き換える。

使い方: update-image-digest.py <kustomization.yaml> <イメージ名> <sha256:...>

- 変更したときは "changed"、すでに同じ値のときは "unchanged" を標準出力に表示する
- コメントや他の行の書式は変更しない (YAML を読み込み直して書き出さず、該当の 1 行だけを置き換える)
- イメージが見つからない、digest の形式が不正、のときは終了コード 1 で失敗する
"""
import re
import sys

DIGEST_PATTERN = re.compile(r"^sha256:[0-9a-f]{64}$")


def main() -> int:
    if len(sys.argv) != 4:
        print(__doc__, file=sys.stderr)
        return 2

    path, image, digest = sys.argv[1:]

    if not DIGEST_PATTERN.fullmatch(digest):
        print(f"invalid digest: {digest!r}", file=sys.stderr)
        return 1

    with open(path, encoding="utf-8") as f:
        lines = f.read().split("\n")

    entry_start = re.compile(rf"^(\s*)- name:\s*{re.escape(image)}\s*$")
    next_entry = re.compile(r"^\s*- name:")

    for i, line in enumerate(lines):
        if not entry_start.match(line):
            continue

        # このイメージの項目は、次の "- name:" の手前までとする
        for j in range(i + 1, len(lines)):
            if next_entry.match(lines[j]):
                break
            m = re.match(r"^(\s*digest:\s*)(\S+)\s*$", lines[j])
            if m:
                if m.group(2) == digest:
                    print("unchanged")
                    return 0
                lines[j] = f"{m.group(1)}{digest}"
                with open(path, "w", encoding="utf-8") as f:
                    f.write("\n".join(lines))
                print("changed")
                return 0

        print(f"digest line not found for image: {image}", file=sys.stderr)
        return 1

    print(f"image not found: {image}", file=sys.stderr)
    return 1


if __name__ == "__main__":
    sys.exit(main())
