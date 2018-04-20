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
        AUTH_PROVIDER_SERVICE_CLIENT_KEY = "${data.vault_generic_secret.auth_provider_service_client_key.data["value"]}"
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
        UK_GOV_NOTIFY_EMAIL_TEMPLATE_VARS = "${var.uk_gov_notify_email_template_vars}"
    }
}

provider "vault" {
    address = "https://vault.reform.hmcts.net:6200"
}

data "vault_generic_secret" "auth_provider_service_client_key" {
    path = "secret/${var.vault_env}/ccidam/service-auth-provider/api/microservice-keys/divorceCcdSubmission"
}

data "vault_generic_secret" "draft_store_api_encryption_key" {
    path = "secret/${var.vault_env}/divorce/draft/encryption_key"
}

data "vault_generic_secret" "uk_gov_notify_api_key" {
    path = "secret/${var.vault_env}/divorce/notify/api_key"
}
