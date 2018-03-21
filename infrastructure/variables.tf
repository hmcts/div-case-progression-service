variable "reform_service_name" {
  default = "evidence-management-client-api"
}

variable "reform_team" {
  default = "divorce"
}

variable "env" {
  type = "string"
}

variable "evidence_management_client_api_port" {
  default = "4006"
}

variable "auth_provider_service_client_baseurl" {
  default = "http://betaDevBccidamS2SLB.reform.hmcts.net:80"
}

variable "auth_provider_service_client_microservice" {
  default = "divorce_document_upload"
}

variable "auth_provider_service_client_key" {
  default = "{{ evidence_management_client_api.secret }}"
}

variable "auth_provider_service_client_tokentimetoliveinseconds" {
  default = "900"
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
