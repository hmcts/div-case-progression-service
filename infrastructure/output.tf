output "vaultName" {
    value = "${local.vaultName}"
}

output "vaultUri" {
  value = "${local.vaultUri}"
}

output "idam_s2s_url" {
  value = "http://${var.idam_s2s_url_prefix}-${var.env}.service.${local.ase_name}.internal"
}
