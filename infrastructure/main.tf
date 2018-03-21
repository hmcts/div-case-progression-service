locals {
  ase_name = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
}

module "app" {
  source       = "git@github.com:hmcts/div-caseprogression-service?ref=develop"
  product      = "${reform_team}-${reform_service_name}"
  location     = "${var.location}"
  env          = "${var.env}"
  ilbIp        = "${var.ilbIp}"
  subscription = "${var.subscription}"
  is_frontend  = false

  app_settings = {
    REFORM_SERVICE_NAME = "${var.reform_service_name }"
    REFORM_TEAM         = "${var.reform_team }"
    REFORM_ENVIRONMENT  = "${var.env}"
    SERVER_PORT         = "${var.evidence_management_client_api_port}"

    #NEW STUFF
    REFORM_SERVICE_NAME                                   = "transformation-service"
    REFORM_TEAM                                           = "divorce"
    REFORM_ENVIRONMENT                                    = "{{ deployment_env }}"
    SERVER_PORT                                           = "{{ transformation_service.port }}"
    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL                  = "{{ service_auth_provider.protocol }}://{{ service_auth_provider.host }}:{{ service_auth_provider.port }}"
    AUTH_PROVIDER_SERVICE_CLIENT_MICROSERVICE             = "{{ transformation_service.microservice_name }}"
    AUTH_PROVIDER_SERVICE_CLIENT_KEY                      = "{{ transformation_service.secret }}"
    AUTH_PROVIDER_SERVICE_CLIENT_TOKENTIMETOLIVEINSECONDS = "{{ transformation_service.tokentimetoliveinseconds }}"
    AUTH_PROVIDER_HEALTH_URI                              = "{{ service_auth_provider.protocol }}://{{ service_auth_provider.host }}:{{ service_auth_provider.port }}/health"
    CCD_CASEDATASTORE_BASEURL                             = "{{ ccd.data_store.protocol }}://{{ ccd.data_store.host }}:{{ ccd.data_store.port }}"
    CCD_CASEDATASTORE_HEALTH_URI                          = "{{ ccd.data_store.protocol }}://{{ ccd.data_store.host }}:{{ ccd.data_store.port }}/status/health"
    CCD_JURISDICTIONID                                    = "{{ ccd_definition.data_store_definition.jurisdictionid }}"
    CCD_CASETYPEID                                        = "{{ ccd_definition.data_store_definition.casetypeid }}"
    CCD_EVENTID_CREATE                                    = "{{ ccd_definition.data_store_definition.eventid.create }}"
    LOGGING_LEVEL_ORG_SRPINGFRAMEWORK_WEB                 = "{{ transformation_service.spring_loglevel }}"
    LOGGING_LEVEL_UK_GOV_HMCTS_CCD                        = "{{ transformation_service.ccd_loglevel }}"
    DRAFT_STORE_API_ENCRYPTION_KEY                        = "{{ transformation_service.draft_encryption_key }}"
    DRAFT_STORE_API_BASEURL                               = "{{ draft_store_api.protocol }}://{{ draft_store_api.host }}:{{ draft_store_api.port }}"
    DRAFT_STORE_API_HEALTH_URI                            = "{{ draft_store_api.protocol }}://{{ draft_store_api.host }}:{{ draft_store_api.port }}/health"
    PDF_GENERATOR_BASE_URL                                = "{{ div_document_generator.protocol }}://{{ div_document_generator.host }}:{{ div_document_generator.port }}"
    UK_GOV_NOTIFY_API_KEY                                 = "{{ transformation_service.uk_gov_notify_api_key }}"
    UK_GOV_NOTIFY_EMAIL_TEMPLATES                         = "{SAVE_DRAFT:'{{ transformation_service.save_draft_notify_template_id }}'}"
  }
}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

data "vault_generic_secret" "auth_provider_service_client_key" {
  path = "secret/test/ccidam/service-auth-provider/api/microservice-keys/divorceDocumentUpload"
}
