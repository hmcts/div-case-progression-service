locals {
    ase_name = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
    pdf_generator_base_url = "http://div-document-generator-${var.env}.service.${local.ase_name}.internal"
    ccd_casedatastore_baseurl = "http://ccd-data-store-api-${var.env}.service.core-compute-${var.env}.internal"
}

module "div-case-progression" {
    source = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
    product = "${var.reform_team}-${var.reform_service_name}"
    location = "${var.location}"
    env = "${var.env}"
    ilbIp = "${var.ilbIp}"
    subscription = "${var.subscription}"
    is_frontend = false

    app_settings = {
        REFORM_SERVICE_NAME = "${var.reform_service_name}"
        REFORM_TEAM = "${var.reform_team}"
        REFORM_ENVIRONMENT = "${var.env}"
        AUTH_PROVIDER_SERVICE_CLIENT_BASEURL = "${var.auth_provider_service_client_baseurl}"
        AUTH_PROVIDER_SERVICE_CLIENT_MICROSERVICE = "${var.auth_provider_service_client_microservice}"
        AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.vault_generic_secret.ccd_submission_s2s_auth_secret.data["value"]}"
        AUTH_PROVIDER_SERVICE_CLIENT_TOKENTIMETOLIVEINSECONDS = "${var.auth_provider_service_client_tokentimetoliveinseconds}"
        AUTH_PROVIDER_HEALTH_URI = "${var.auth_provider_service_client_baseurl}/health"
        CCD_CASEDATASTORE_BASEURL = "${local.ccd_casedatastore_baseurl}"
        CCD_CASEDATASTORE_HEALTH_URI = "${local.ccd_casedatastore_baseurl}/health"
        CCD_JURISDICTIONID = "${var.ccd_jurisdictionid}"
        CCD_CASETYPEID = "${var.ccd_casetypeid}"
        CCD_EVENTID_CREATE = "${var.ccd_eventid_create}"
        LOGGING_LEVEL_ORG_SRPINGFRAMEWORK_WEB = "${var.logging_level_org_springframework_web}"
        LOGGING_LEVEL_UK_GOV_HMCTS_CCD = "${var.logging_level_uk_gov_hmcts_ccd}"
        PDF_GENERATOR_BASE_URL = "${local.pdf_generator_base_url}"
        PDF_GENERATOR_HEALTHURL = "${local.pdf_generator_base_url}/health"
        DRAFT_STORE_API_ENCRYPTION_KEY = "${data.vault_generic_secret.draft_store_api_encryption_key.data["value"]}"
        DRAFT_STORE_API_BASEURL = "${var.draft_store_api_baseurl}"
        DRAFT_STORE_API_HEALTH_URI = "${var.draft_store_api_baseurl}/health"
        UK_GOV_NOTIFY_API_KEY = "${data.vault_generic_secret.uk_gov_notify_api_key.data["value"]}"
        UK_GOV_NOTIFY_EMAIL_TEMPLATES = "${var.uk_gov_notify_email_templates}"
    }
}

provider "vault" {
    address = "https://vault.reform.hmcts.net:6200"
}

module "key-vault" {
    source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
    name                = "${var.reform_team}-cps-${var.env}"
    product             = "${var.product}"
    env                 = "${var.env}"
    tenant_id           = "${var.tenant_id}"
    object_id           = "${var.jenkins_AAD_objectId}"
    resource_group_name = "${module.div-case-progression.resource_group_name}"
    # dcd_cc-dev group object ID
    product_group_object_id = "1c4f0704-a29e-403d-b719-b90c34ef14c9"
}

data "vault_generic_secret" "ccd_submission_s2s_auth_secret" {
    path = "secret/${var.vault_env}/ccidam/service-auth-provider/api/microservice-keys/divorceDocumentGenerator"
}

data "vault_generic_secret" "draft_store_api_encryption_key" {
    path = "secret/${var.vault_env}/divorce/draft/encryption_key"
}

data "vault_generic_secret" "uk_gov_notify_api_key" {
    path = "secret/${var.vault_env}/divorce/notify/api_key"
}

resource "azurerm_key_vault_secret" "ccd_submission_s2s_auth_secret" {
    name      = "ccd_submission_s2s_auth_secret"
    value     = "${data.vault_generic_secret.ccd_submission_s2s_auth_secret.data["value"]}"
    vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "draft_store_api_encryption_key" {
    name      = "draft_store_api_encryption_key"
    value     = "${data.vault_generic_secret.draft_store_api_encryption_key.data["value"]}"
    vault_uri = "${module.key-vault.key_vault_uri}"
}
