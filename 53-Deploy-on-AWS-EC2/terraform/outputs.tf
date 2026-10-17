output "public_ip" {
  description = "Public IPv4 address of the API instance."
  value       = aws_instance.api.public_ip
}

output "api_url" {
  description = "HTTP URL of the deployed API."
  value       = "http://${aws_instance.api.public_ip}:8080/api/status"
}

output "ssh_command" {
  description = "SSH command template; replace the private-key path with your local key file."
  value       = "ssh -i <path-to-private-key.pem> ubuntu@${aws_instance.api.public_ip}"
}
