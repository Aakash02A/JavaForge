provider "aws" {
  region = var.aws_region
}

data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}

resource "aws_vpc" "api" {
  cidr_block           = "10.53.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "java-forge-ec2-api"
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.api.id
  cidr_block              = "10.53.1.0/24"
  availability_zone       = data.aws_availability_zones.available.names[0]
  map_public_ip_on_launch = true

  tags = {
    Name = "java-forge-ec2-api-public"
  }
}

resource "aws_internet_gateway" "api" {
  vpc_id = aws_vpc.api.id

  tags = {
    Name = "java-forge-ec2-api"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.api.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.api.id
  }

  tags = {
    Name = "java-forge-ec2-api-public"
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "api" {
  name_prefix = "java-forge-ec2-api-"
  description = "Allow HTTP API access and SSH from the configured administrator IP."
  vpc_id      = aws_vpc.api.id

  ingress {
    description = "SSH from the configured administrator IP only"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.ssh_cidr]
  }

  ingress {
    description = "Demo API"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "java-forge-ec2-api"
  }
}

resource "aws_instance" "api" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.instance_type
  key_name                    = var.key_name
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.api.id]
  associate_public_ip_address = true
  metadata_options {
    http_endpoint               = "enabled"
    http_tokens                 = "required"
    http_put_response_hop_limit = 1
  }

  user_data = <<-USER_DATA
    #!/bin/bash
    set -euxo pipefail
    apt-get update
    DEBIAN_FRONTEND=noninteractive apt-get install -y docker.io
    systemctl enable --now docker
  USER_DATA

  user_data_replace_on_change = true

  root_block_device {
    volume_type           = "gp3"
    volume_size           = 12
    encrypted             = true
    delete_on_termination = true
  }

  tags = {
    Name = "java-forge-ec2-api"
  }

  depends_on = [aws_route_table_association.public]
}
