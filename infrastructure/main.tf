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

    CASE_DATA_STORE_BASEURL=
    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL=
    AUTH_PROVIDER_SERVICE_CLIENT_KEY=
    AUTH_PROVIDER_SERVICE_CLIENT_MICROSERVICE=
    DRAFT_STORE_API_BASEURL=
    PDF_GENERATOR_BASE_URL=
    REFORM_SERVICE_NAME                                   = "${var.reform_service_name }"
    REFORM_TEAM                                           = "${var.reform_team }"
    REFORM_ENVIRONMENT                                    = "${var.env}"
    SERVER_PORT                                           = "${var.evidence_management_client_api_port}"
  }
}

provider "vault" {
  address = "https://vault.reform.hmcts.net:6200"
}

data "vault_generic_secret" "auth_provider_service_client_key" {
  path = "secret/test/ccidam/service-auth-provider/api/microservice-keys/divorceDocumentUpload"
}
