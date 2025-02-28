
resource "aws_instance" "monitoring_docker" {
  ami                    = data.aws_ami.ubuntu_ami.id # Ubuntu amd64 (x86_64)
  instance_type          = var.instance_type         # Free tier
  vpc_security_group_ids = [aws_security_group.allow_web.id]
  key_name               = "ssh_instance_key" # Please use your key name
  iam_instance_profile = aws_iam_instance_profile.monitoring_profile_devops_course.name
  root_block_device {
    volume_size = 40 # Adjust size based on needs, in GB
    volume_type = "gp3"
  }
  tags = {
    Name = "Monitoring on the docker"
  }
}