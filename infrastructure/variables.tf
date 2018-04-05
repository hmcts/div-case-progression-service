variable "reform_service_name" {
    default = "case-progression"
}

variable "reform_team" {
    default = "div"
}

variable "env" {
    type = "string"
}

variable "case_progression_service_port" {
    default = "4003"
}

variable "case_progression_external_servicer_port" {
    default = "443"
}

variable "case_progression_service_url" {
    default = "https://div-case-progression-sandbox-staging.service.core-compute-sandbox.internal"
}

variable "auth_idam_client_baseUrl" {
    default = "http://betaDevBccidamAppLB.reform.hmcts.net"
}

variable "auth_provider_service_client_port" {
    default = "4052"
}

variable "auth_provider_service_client_baseurl" {
    default = "http://betaDevBccidamS2SLB.reform.hmcts.net:80"
}

variable "auth_provider_service_client_microservice" {
    default = "divorce_ccd_submission"
}

variable "auth_provider_service_client_key" {
    default = "{{ evidence_management_client_api.secret }}"
}

variable "auth_provider_service_client_tokentimetoliveinseconds" {
    default = "900"
}

variable "ccd_casedatastore_baseurl" {
    default = "https://case-data-app.test.ccd.reform.hmcts.net:4481"
}

variable "ccd_casedatagw_baseuri" {
    default = "https://case-api-gateway-web.test.ccd.reform.hmcts.net"
}

variable "ccd_jurisdictionid" {
    default = "DIVORCE"
}

variable "ccd_casetypeid" {
    default = "DIVORCE"
}

variable "ccd_eventid_create" {
    default = "create"
}

variable "logging_level_org_springframework_web" {
    type = "string"
}

variable "logging_level_uk_gov_hmcts_ccd" {
    type = "string"
}

variable "draft_store_api_baseurl" {
    type = "string"
}

variable "pdf_generator_base_url" {
    default = "string"
}

variable "uk_gov_notify_email_templates" {
    type = "string"
}

variable "no_proxy" {
    default = "localhost,127.0.0.0/8,127.0.0.1,127.0.0.1*,local.home,reform.hmcts.net,*.reform.hmcts.net,internal,*.internal"
}

variable "subscription" {}

variable "location" {
    type = "string"
    default = "UK South"
}

variable "ilbIp" {}

variable "vault_env" {}
