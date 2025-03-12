provider "aws" {
  region = var.region
}

resource "aws_instance" "monitoring_vm" {
  ami           = data.aws_ami.ubuntu_ami.id  # Ubuntu 22.04
  instance_type = var.instance_type
  key_name      = "ssh_instance_key"
  security_groups = [aws_security_group.monitoring_sg.name]

  tags = {
    Name = "monitoring-vm"
  }

  provisioner "remote-exec" {
    inline = [
      "sudo apt update && sudo apt install -y python3"
    ]

    connection {
      type        = "ssh"
      user        = "ubuntu"
      private_key = file("~/.ssh/ssh_instance_key.pem")
      host        = self.public_ip
    }
  }
}

resource "aws_security_group" "monitoring_sg" {
  name        = "monitoring_sg"
  description = "Allow access to monitoring services"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 9100
    to_port     = 9100
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
