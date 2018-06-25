output "vaultName" {
    value = "${local.vaultName}"
}

output "vaultUri" {
    value = "${module.key-vault.key_vault_uri}"
}

output "idam_s2s_url" {
    value = "http://${var.idam_s2s_url_prefix}-${var.env}.service.${local.ase_name}.internal"
}

output "draft_store_service_url" {
    value = "${local.draft_store_api_baseurl}"
}
