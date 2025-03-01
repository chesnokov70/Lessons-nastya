provider "aws" {
  region = var.region
}


resource "aws_launch_template" "node_exporter" {
  name_prefix   = "node-exporter"
  image_id      = data.aws_ami.ubuntu_ami.id  # Ubuntu 20.04 AMI (Update if needed)
  instance_type = var.instance_type

  #user_data = base64encode(templatefile("./node_exporter_install.sh.tpl", {}))
  user_data = base64encode(templatefile("${path.module}/node_exporter_install.sh.tpl", {}))

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name  = "node-exporter-instance"
      Owner = "Sergei Ches"
    }
  }
}

resource "aws_autoscaling_group" "node_exporter" {
  desired_capacity     = 2
  max_size            = 5
  min_size            = 2
  health_check_type   = "EC2"  # Ensures instances are checked properly
  force_delete        = true   # Allows safe deletion

  launch_template {
    id      = aws_launch_template.node_exporter.id
    version = "$Latest"  # Corrected syntax
  }

  # Use dynamically fetched subnets
  vpc_zone_identifier = data.aws_subnets.vpcsubnets.ids
}



