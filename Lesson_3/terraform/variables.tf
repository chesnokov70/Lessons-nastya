variable "domain_name" {
  default = "ches-it.com"
}


variable "region" {
  description = "Please Enter AWS Region to deploy Server"
  type        = string
  default     = "us-east-1"
}

variable "instance_type" {
  description = "Enter Instance Type"
  type        = string
  default     = "t3.small"
}


variable "allow_ports" {
  description = "List of Ports to open for server"
  type        = list(any)
  default     = ["80", "443", "22", "587", "3000", "3100", "8080", "9093", "9090", "9100"]
}

variable "enable_detailed_monitoring" {
  type    = bool
  default = false
}


variable "common_tags" {
  description = "Common Tags to apply to all resources"
  type        = map(any)
  default = {
    Owner       = "Sergei Ches"
    Project     = "Monitoring"
    CostCenter  = "12345"
    Environment = "Monitoring"
  }
}

variable "NODE_EXPORTER_VERSION" {
  description = "The version of Node Exporter to install"
  type        = string
  default     = "1.3.1"  # Set the default version here, or leave it to be provided
}
