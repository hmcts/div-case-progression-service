locals {
    ase_name = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"

    local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"

    pdf_generator_base_url = "http://div-dgs-${local.local_env}.service.core-compute-${local.local_env}.internal"
    fees_and_payments_base_url= "http://div-fps-${local.local_env}.service.core-compute-${local.local_env}.internal"
    payment_api_base_url= "http://payment-api-${local.local_env}.service.core-compute-${local.local_env}.internal"
    ccd_casedatastore_baseurl = "http://ccd-data-store-api-${local.local_env}.service.core-compute-${local.local_env}.internal"
    draft_store_api_baseurl = "http://draft-store-service-${local.local_env}.service.core-compute-${local.local_env}.internal"
    dm_store_url = "http://dm-store-${local.local_env}.service.core-compute-${local.local_env}.internal"
    idam_s2s_url = "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.core-compute-${local.local_env}.internal"
    div_validation_service_url = "http://div-vs-${local.local_env}.service.core-compute-${local.local_env}.internal"

    previewVaultName = "${var.product}-${var.reform_service_name}"
    nonPreviewVaultName = "${var.reform_team}-${var.reform_service_name}-${var.env}"
    vaultName = "${var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName}"

    vaultUri = "${data.azurerm_key_vault.div_key_vault.vault_uri}"
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
    common_tags                     = "${var.common_tags}"

    is_frontend = false

    app_settings = {
        REFORM_SERVICE_NAME = "${var.reform_service_name}"
        REFORM_TEAM = "${var.reform_team}"
        REFORM_ENVIRONMENT = "${var.env}"
        AUTH_PROVIDER_SERVICE_CLIENT_BASEURL = "${local.idam_s2s_url}"
        AUTH_SERVICE_CLIENT_DIVORCE_CCD_SUBMISSION_NAME = "${var.auth_provider_service_client_microservice_div_ccd_submission}"
        AUTH_SERVICE_CLIENT_DIVORCE_FRONTEND_NAME = "${var.auth_provider_service_client_microservice_div_frontend}"
        AUTH_SERVICE_DIVORCE_CCD_SUBMISSION_KEY = "${data.azurerm_key_vault_secret.ccd-submission-s2s-auth-secret.value}"
        AUTH_SERVICE_DIVORCE_FRONTEND_KEY = "${data.azurerm_key_vault_secret.frontend_secret.value}"
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
        DRAFT_STORE_API_ENCRYPTION_KEY = "${data.azurerm_key_vault_secret.draft-store-api-encryption-key.value}"
        DRAFT_STORE_API_BASEURL = "${local.draft_store_api_baseurl}"
        DRAFT_STORE_API_HEALTH_URI = "${local.draft_store_api_baseurl}/health"
        UK_GOV_NOTIFY_API_KEY = "${data.azurerm_key_vault_secret.uk-gov-notify-api-key.value}"
        UK_GOV_NOTIFY_EMAIL_TEMPLATES = "${var.uk_gov_notify_email_templates}"
        UK_GOV_NOTIFY_EMAIL_TEMPLATE_VARS = "${var.uk_gov_notify_email_template_vars}"
        DOCUMENT_MANAGEMENT_STORE_URL = "${local.dm_store_url}"
        IDAM_API_BASEURL = "${var.idam_api_baseurl}"
        IDAM_API_HEALTH_URI = "${var.idam_api_baseurl}/health"
        PAYMENT_API_BASEURL = "${local.payment_api_base_url}"
        FEES_AND_PAYMENTS_BASE_URL="${local.fees_and_payments_base_url}"
        DRAFT_CCD_CHECK_ENABLED = "${var.draft_check_ccd_enabled}"
        DIV_VALIDATION_SERVICE_URL = "${local.div_validation_service_url}"
    }
}

provider "vault" {
    address = "https://vault.reform.hmcts.net:6200"
}

data "azurerm_key_vault" "div_key_vault" {
    name                = "${local.vaultName}"
    resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault_secret" "frontend_secret" {
    name      = "frontend-secret"
    vault_uri = "${data.azurerm_key_vault.div_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "ccd-submission-s2s-auth-secret" {
    name      = "ccd-submission-s2s-auth-secret"
    vault_uri = "${data.azurerm_key_vault.div_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "div-doc-s2s-auth-secret" {
    name      = "div-doc-s2s-auth-secret"
    vault_uri = "${data.azurerm_key_vault.div_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "draft-store-api-encryption-key" {
    name      = "draft-store-api-encryption-key"
    vault_uri = "${data.azurerm_key_vault.div_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "uk-gov-notify-api-key" {
    name      = "uk-gov-notify-api-key"
    vault_uri = "${data.azurerm_key_vault.div_key_vault.vault_uri}"
}
