locals {
    ase_name = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

    local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"

    pdf_generator_base_url = "http://div-dgs-${local.local_env}.service.core-compute-${local.local_env}.internal"
    ccd_casedatastore_baseurl = "http://ccd-data-store-api-${local.local_env}.service.core-compute-${local.local_env}.internal"
    draft_store_api_baseurl = "http://draft-store-service-${local.local_env}.service.core-compute-${local.local_env}.internal"
    dm_store_url = "http://dm-store-${local.local_env}.service.core-compute-${local.local_env}.internal"
    idam_s2s_url = "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.core-compute-${local.local_env}.internal"
    div_validation_service_url = "http://div-vs-${local.local_env}.service.core-compute-${local.local_env}.internal"

    previewVaultName = "${var.product}-${var.reform_service_name}"
    nonPreviewVaultName = "${var.reform_team}-${var.reform_service_name}-${var.env}"
    vaultName = "${var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName}"

    nonPreviewVaultUri = "${module.key-vault.key_vault_uri}"
    previewVaultUri = "https://div-${var.reform_service_name}-aat.vault.azure.net/"
    vaultUri = "${var.env == "preview"? local.previewVaultUri : local.nonPreviewVaultUri}"
}

module "div-case-progression" {
    source                          = "git@github.com:hmcts/moj-module-webapp.git"
    product                         = "${var.product}-${var.reform_service_name}"
    location                        = "${var.location}"
    env                             = "${var.env}"
    ilbIp                           = "${var.ilbIp}"
    appinsights_instrumentation_key = "${var.appinsights_instrumentation_key}"
    subscription                    = "${var.subscription}"
    capacity                        = "${var.capacity}"

    is_frontend = false

    app_settings = {
        REFORM_SERVICE_NAME = "${var.reform_service_name}"
        REFORM_TEAM = "${var.reform_team}"
        REFORM_ENVIRONMENT = "${var.env}"
        AUTH_PROVIDER_SERVICE_CLIENT_BASEURL = "${local.idam_s2s_url}"
        AUTH_PROVIDER_SERVICE_CLIENT_MICROSERVICE = "${var.auth_provider_service_client_microservice}"
        AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.vault_generic_secret.ccd-submission-s2s-auth-secret.data["value"]}"
        AUTH_PROVIDER_SERVICE_CLIENT_TOKENTIMETOLIVEINSECONDS = "${var.auth_provider_service_client_tokentimetoliveinseconds}"
        AUTH_PROVIDER_HEALTH_URI = "${local.idam_s2s_url}/health"
        CCD_CASEDATASTORE_BASEURL = "${local.ccd_casedatastore_baseurl}"
        CCD_CASEDATASTORE_HEALTH_URI = "${local.ccd_casedatastore_baseurl}/health"
        CCD_JURISDICTIONID = "${var.ccd_jurisdictionid}"
        CCD_CASETYPEID = "${var.ccd_casetypeid}"
        CCD_EVENTID_CREATE = "${var.ccd_eventid_create}"
        LOGGING_LEVEL_ORG_SRPINGFRAMEWORK_WEB = "${var.logging_level_org_springframework_web}"
        LOGGING_LEVEL_UK_GOV_HMCTS_CCD = "${var.logging_level_uk_gov_hmcts_ccd}"
        PDF_GENERATOR_BASE_URL = "${local.pdf_generator_base_url}"
        PDF_GENERATOR_HEALTHURL = "${local.pdf_generator_base_url}/health"
        DRAFT_STORE_API_ENCRYPTION_KEY = "${data.vault_generic_secret.draft-store-api-encryption-key.data["value"]}"
        DRAFT_STORE_API_BASEURL = "${local.draft_store_api_baseurl}"
        DRAFT_STORE_API_HEALTH_URI = "${local.draft_store_api_baseurl}/health"
        UK_GOV_NOTIFY_API_KEY = "${data.vault_generic_secret.uk-gov-notify-api-key.data["value"]}"
        UK_GOV_NOTIFY_EMAIL_TEMPLATES = "${var.uk_gov_notify_email_templates}"
        UK_GOV_NOTIFY_EMAIL_TEMPLATE_VARS = "${var.uk_gov_notify_email_template_vars}"
        DOCUMENT_MANAGEMENT_STORE_URL = "${local.dm_store_url}"
        IDAM_API_BASEURL = "${var.idam_api_baseurl}"
        IDAM_API_HEALTH_URI = "${var.idam_api_baseurl}/health"
        DRAFT_CCD_CHECK_ENABLED = "${var.draft_check_ccd_enabled}"
        DIV_VALIDATION_SERVICE_URL = "${local.div_validation_service_url}"
    }
}

provider "vault" {
    address = "https://vault.reform.hmcts.net:6200"
}

module "key-vault" {
    source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
    name                = "${local.vaultName}"
    product             = "${var.product}"
    env                 = "${var.env}"
    tenant_id           = "${var.tenant_id}"
    object_id           = "${var.jenkins_AAD_objectId}"
    resource_group_name = "${module.div-case-progression.resource_group_name}"
    # dcd_cc-dev group object ID
    product_group_object_id = "1c4f0704-a29e-403d-b719-b90c34ef14c9"
}

data "vault_generic_secret" "ccd-submission-s2s-auth-secret" {
    path = "secret/${var.vault_env}/ccidam/service-auth-provider/api/microservice-keys/divorceCcdSubmission"
}

data "vault_generic_secret" "div-doc-s2s-auth-secret" {
    path = "secret/${var.vault_env}/ccidam/service-auth-provider/api/microservice-keys/divorceDocumentGenerator"
}

data "vault_generic_secret" "draft-store-api-encryption-key" {
    path = "secret/${var.vault_env}/divorce/draft/encryption_key"
}

data "vault_generic_secret" "uk-gov-notify-api-key" {
    path = "secret/${var.vault_env}/divorce/notify/api_key"
}

resource "azurerm_key_vault_secret" "ccd-submission-s2s-auth-secret" {
    name      = "ccd-submission-s2s-auth-secret"
    value     = "${data.vault_generic_secret.ccd-submission-s2s-auth-secret.data["value"]}"
    vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "div-doc-s2s-auth-secret" {
    name      = "div-doc-s2s-auth-secret"
    value     = "${data.vault_generic_secret.div-doc-s2s-auth-secret.data["value"]}"
    vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "draft-store-api-encryption-key" {
    name      = "draft-store-api-encryption-key"
    value     = "${data.vault_generic_secret.draft-store-api-encryption-key.data["value"]}"
    vault_uri = "${module.key-vault.key_vault_uri}"
}
