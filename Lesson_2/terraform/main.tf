provider "aws" {
  region = var.region
}

resource "aws_launch_template" "node_exporter" {
  name_prefix   = "node-exporter"
  image_id      = data.aws_ami.ubuntu_ami.id # Ubuntu 20.04 AMI (Update if needed)
  instance_type = var.instance_type
  key_name      = "ssh_instance_key" # Please use your key name  

  # Attach Security Group
  network_interfaces {
    associate_public_ip_address = true                              # Enable if instances need public IP
    security_groups             = [aws_security_group.allow_web.id] # Attach Security Group
  }

    # Pass the NODE_EXPORTER_VERSION variable into the template
  user_data = base64encode(templatefile("${path.module}/node_exporter_install.sh.tpl", {
    NODE_EXPORTER_VERSION = var.NODE_EXPORTER_VERSION
  }))

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name  = "node-exporter-instance"
      Owner = "Sergei Ches"
    }
  }
}

resource "aws_autoscaling_group" "node_exporter" {
  desired_capacity  = 2
  max_size          = 5
  min_size          = 2
  health_check_type = "EC2" # Ensures instances are checked properly
  force_delete      = true  # Allows safe deletion
  # Use dynamically fetched subnets
  vpc_zone_identifier = [aws_subnet.subnet_a.id, aws_subnet.subnet_b.id]  # Correcting resource names
  launch_template {
    id      = aws_launch_template.node_exporter.id
    version = "$Latest" # Corrected syntax
  }


  tag {
    key                 = "Environment"
    value               = "prod"
    propagate_at_launch = true
  }

  tag {
    key                 = "Name"
    value               = "prod-servers"
    propagate_at_launch = true
  }

}



