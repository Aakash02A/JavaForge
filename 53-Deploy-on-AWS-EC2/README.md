# 53 - Deploy on AWS EC2

Provision an Ubuntu EC2 instance with Terraform, build a containerized Spring Boot API, and deploy the image to the instance over SSH. Terraform restricts SSH ingress to one configured IPv4 address; the demo API listens on port `8080`.

> **AWS costs:** EC2 instances, EBS volumes, public IPv4 addresses, and data transfer may incur charges. Check current AWS pricing and remove the resources when finished. This is a learning example, not a production deployment blueprint.

## What is included

- Java 21 / Spring Boot API with `/api/status` and `/health` endpoints.
- Multi-stage Dockerfile; the runtime process runs as a non-root user.
- Terraform configuration for a dedicated VPC/public subnet, Ubuntu 24.04 EC2 instance, encrypted root volume, IMDSv2, and security group.
- User-data bootstrap that installs and enables Docker on first boot.
- `deploy.sh` to build locally, stream the image to EC2, and run it with a restart policy.
- `.gitignore` rules for Terraform state, variable files, build output, and local secrets.

## Prerequisites

- An AWS account and AWS CLI credentials configured locally, for example with `aws configure` or an AWS profile. Do not put access keys in this repository.
- Terraform 1.5 or newer.
- Docker Engine.
- OpenSSH client and Bash (Linux, macOS, WSL, or Git Bash).
- An existing EC2 key pair in the AWS region you will use; keep its private key secure and never commit it.
- An IPv4 address from which you will SSH to the instance.

Terraform creates an isolated VPC and public subnet, then discovers the latest Canonical Ubuntu 24.04 x86_64 AMI in the selected region. The instance uses the subnet's public route through an internet gateway.

## Configure infrastructure

From this project directory, copy the example variables file:

```bash
cp terraform/terraform.tfvars.example terraform/terraform.tfvars
```

Edit `terraform/terraform.tfvars`:

```hcl
aws_region    = "us-east-1"
instance_type = "t3.micro"
key_name      = "your-existing-ec2-key-pair-name"
ssh_cidr      = "YOUR_PUBLIC_IPV4/32"
```

Use your current public IPv4 address with `/32` (one address only), not the example documentation address. Choose a region where the EC2 key pair exists. The default instance type is an example only; confirm its availability and cost for your account and region.

Initialize Terraform, inspect the plan, and provision the host:

```bash
terraform -chdir=terraform init
terraform -chdir=terraform fmt -check
terraform -chdir=terraform validate
terraform -chdir=terraform plan
terraform -chdir=terraform apply
```

Review the plan before confirming `apply`. Terraform state can contain infrastructure details; it is ignored by Git and must be stored securely. The instance bootstraps Docker through cloud-init. Allow several minutes after creation for first-boot package installation to finish.

Get the address Terraform assigned:

```bash
terraform -chdir=terraform output -raw public_ip
```

## Deploy the API

From Bash at the project root, pass the output address and the path to the private key that matches `key_name`:

```bash
bash deploy.sh "$(terraform -chdir=terraform output -raw public_ip)" "$HOME/.ssh/your-key.pem"
```

The script builds the image locally and streams it over SSH to the EC2 host; no container registry is required. It replaces the container named `java-forge-api` and publishes port `8080`.

Check the API:

```bash
curl "http://$(terraform -chdir=terraform output -raw public_ip):8080/health"
curl "http://$(terraform -chdir=terraform output -raw public_ip):8080/api/status"
```

Example responses:

```json
{"status":"UP"}
```

```json
{"status":"running","message":"Hello from AWS EC2"}
```

To redeploy after changing the application, rerun `bash deploy.sh` with the same instance address and key.

## Security notes

- Port 22 is open only to `ssh_cidr`; update it if your public IP changes.
- Port 8080 is publicly reachable for this HTTP learning demo. Do not use it to expose sensitive or production data.
- The example does not configure HTTPS, a domain, a load balancer, backups, or centralized secrets. Add those before production use.
- The EC2 instance receives no AWS IAM permissions because this sample transfers the image over SSH.
- Never commit private SSH keys, AWS credentials, Terraform state, or `terraform.tfvars`.

## Run and test locally

Run the API directly:

```bash
mvn spring-boot:run
```

Build the container locally:

```bash
docker build -t java-forge/aws-ec2-api:latest .
docker run --rm -p 8080:8080 java-forge/aws-ec2-api:latest
```

Run the automated web-layer tests:

```bash
mvn test
```

The test suite does not need AWS, Terraform, Docker, or credentials.

## Tear down AWS resources

When finished, destroy the instance, security group, and attached root volume:

```bash
terraform -chdir=terraform destroy
```

Review the destroy plan before confirming. Keep the EC2 key pair only if you use it for other instances.

## Project structure

```text
Dockerfile
deploy.sh
pom.xml
terraform/
  main.tf
  outputs.tf
  terraform.tfvars.example
  variables.tf
  versions.tf
src/
  main/java/com/javaforge/awsdeployment/
  test/java/com/javaforge/awsdeployment/
```
