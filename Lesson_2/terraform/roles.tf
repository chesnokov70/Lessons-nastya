
resource "aws_iam_role" "node_exporter_role_devops_course" {
  name = "node_exporter_role_devops_course"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "node_exporter_monitoring_policy" {
  name        = "node_exporter_monitoring_policy"
  description = "Policy to allow EC2 to access monitoring services"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ec2:Describe*",
          "ec2:GetSecurityGroupsForVpc"
        ]
        Resource = "*"
      },
      {
        Effect   = "Allow"
        Action   = "elasticloadbalancing:Describe*"
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "cloudwatch:ListMetrics",
          "cloudwatch:GetMetricStatistics",
          "cloudwatch:Describe*"
        ]
        Resource = "*"
      },
      {
        Effect   = "Allow"
        Action   = "autoscaling:Describe*"
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "node_exporter_policy_attach" {
  role       = aws_iam_role.node_exporter_role_devops_course.name
  policy_arn = aws_iam_policy.node_exporter_monitoring_policy.arn
}

resource "aws_iam_instance_profile" "node_exporter_profile_devops_course" {
  name = "node_exporter_profile_devops_course"
  role = aws_iam_role.node_exporter_role_devops_course.name
}
