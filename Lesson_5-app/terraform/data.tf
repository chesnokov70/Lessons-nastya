data "aws_ami" "ubuntu_ami" {
  most_recent = true
  
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"]
  }
  
  filter {
    name = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "owner-id"
    values = ["099720109477"] # Canonical's AWS account ID
  }

  filter {
    name   = "state"
    values = ["available"]
  }

}

data "aws_vpc" "default" {
  default = "true"
}

data "aws_subnets" "vpcsubnets" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
  filter {
    name   = "default-for-az"
    values = [true]
  }
  filter {
    name   = "state"
    values = ["available"]
  }
}

data "aws_subnet" "vpcsubnet" {
  for_each = { for index, subnetid in data.aws_subnets.vpcsubnets.ids : index => subnetid }
  id       = each.value
}

data "aws_caller_identity" "current" {}