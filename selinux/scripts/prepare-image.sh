#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 /path/to/Fedora-Cloud-Base-*.qcow2" >&2
  exit 64
fi

base_image=$1
root_dir=$(cd "$(dirname "$0")/.." && pwd)
lab_dir="$root_dir/lab"

if [[ ! -f "$base_image" ]]; then
  echo "Base image not found: $base_image" >&2
  exit 66
fi

command -v qemu-img >/dev/null || { echo "qemu-img is required." >&2; exit 69; }
mkdir -p "$lab_dir"
rm -f "$lab_dir/fedora.qcow2" "$lab_dir/seed.iso"
qemu-img create -f qcow2 -F qcow2 -b "$(cd "$(dirname "$base_image")" && pwd)/$(basename "$base_image")" "$lab_dir/fedora.qcow2"

if command -v genisoimage >/dev/null; then
  genisoimage -quiet -output "$lab_dir/seed.iso" -volid cidata -joliet -rock "$root_dir/cloud-init/user-data" "$root_dir/cloud-init/meta-data"
elif command -v mkisofs >/dev/null; then
  mkisofs -quiet -output "$lab_dir/seed.iso" -volid cidata -joliet -rock "$root_dir/cloud-init/user-data" "$root_dir/cloud-init/meta-data"
elif command -v hdiutil >/dev/null; then
  hdiutil makehybrid -quiet -o "$lab_dir/seed.iso" -iso -joliet -default-volume-name cidata "$root_dir/cloud-init"
else
  echo "Install genisoimage, mkisofs, or use macOS hdiutil." >&2
  exit 69
fi

echo "Created $lab_dir/fedora.qcow2 and $lab_dir/seed.iso"
