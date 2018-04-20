variable "reform_service_name" {
    default = "case-progression"
}

variable "reform_team" {
    default = "div"
}

variable "env" {
    type = "string"
}

variable "auth_provider_service_client_baseurl" {}

variable "auth_provider_service_client_microservice" {
    default = "divorce_ccd_submission"
}

variable "auth_provider_service_client_key" {
    default = "{{ evidence_management_client_api.secret }}"
}

variable "auth_provider_service_client_tokentimetoliveinseconds" {
    default = "900"
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

variable "uk_gov_notify_email_templates" {
    type = "string"
}

variable "uk_gov_notify_email_template_vars" {
    type = "string"
}

variable "subscription" {}

variable "location" {
    type = "string"
    default = "UK South"
}

variable "ilbIp" {}

variable "vault_env" {}
