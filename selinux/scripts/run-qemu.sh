#!/usr/bin/env bash
set -euo pipefail

root_dir=$(cd "$(dirname "$0")/.." && pwd)
disk="$root_dir/lab/fedora.qcow2"
seed="$root_dir/lab/seed.iso"
memory=${MEMORY:-2048}
cpus=${CPUS:-2}

[[ -f "$disk" && -f "$seed" ]] || { echo "Run scripts/prepare-image.sh first." >&2; exit 66; }
common=( -nographic -m "$memory" -smp "$cpus" -drive "file=$disk,if=virtio,format=qcow2" -drive "file=$seed,media=cdrom,readonly=on" -nic "user,model=virtio-net-pci,hostfwd=tcp:127.0.0.1:2222-:22" )

case "$(uname -m)" in
  x86_64)
    accel=()
    if [[ -e /dev/kvm ]]; then accel=( -enable-kvm -cpu host ); fi
    exec qemu-system-x86_64 "${accel[@]}" -machine q35 "${common[@]}"
    ;;
  arm64|aarch64)
    : "${EFI_CODE:?Set EFI_CODE to an AArch64 QEMU UEFI firmware file.}"
    [[ -f "$EFI_CODE" ]] || { echo "EFI_CODE not found: $EFI_CODE" >&2; exit 66; }
    exec qemu-system-aarch64 -machine virt -cpu cortex-a72 -bios "$EFI_CODE" "${common[@]}"
    ;;
  *) echo "Unsupported host architecture: $(uname -m)" >&2; exit 69 ;;
esac
