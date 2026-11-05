variable "aws_region" {
  description = "AWS region where the EC2 instance will be created."
  type        = string
  default     = "us-east-1"
}

variable "instance_type" {
  description = "EC2 instance type. Check current regional pricing before deployment."
  type        = string
  default     = "t3.micro"
}

variable "key_name" {
  description = "Name of an existing EC2 key pair used for SSH access."
  type        = string

  validation {
    condition     = length(trimspace(var.key_name)) > 0
    error_message = "Set key_name to an existing EC2 key pair."
  }
}

variable "ssh_cidr" {
  description = "Your public IPv4 address in CIDR notation, allowed to connect over SSH (port 22)."
  type        = string

  validation {
    condition     = can(cidrnetmask(var.ssh_cidr)) && endswith(var.ssh_cidr, "/32")
    error_message = "ssh_cidr must be one IPv4 address with a /32 mask, for example 203.0.113.10/32."
  }
}
