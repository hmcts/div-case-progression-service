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

variable ccd_casedatastore_baseurl {
  default = "{{ ccd.data_store.protocol }}://{{ ccd.data_store.host }}:{{ ccd.data_store.port }}"
}

variable ccd_casedatastore_health_uri {
  default = "{{ ccd.data_store.protocol }}://{{ ccd.data_store.host }}:{{ ccd.data_store.port }}/status/health"
}

variable ccd_jurisdictionid {
  default = "{{ ccd_definition.data_store_definition.jurisdictionid }}"
}

variable ccd_casetypeid {
  default = "{{ ccd_definition.data_store_definition.casetypeid }}"
}

variable ccd_eventid_create {
  default = "{{ ccd_definition.data_store_definition.eventid.create }}"
}

variable "evidence_management_upload_file_url" {
  default = "https://api-gateway.test.dm.reform.hmcts.net/documents"
}

variable "document_management_store_url" {
  default = "https://api.test.dm.reform.hmcts.net:4604"
}

variable "evidence_management_health_url" {
  default = "https://api-gateway.test.dm.reform.hmcts.net/health"
}

variable "http_connect_timeout" {
  default = "60000"
}

variable "http_connect_request_timeout" {
  default = "60000"
}

variable "http_connect_socket_timeout" {
  default = "1000"
}
