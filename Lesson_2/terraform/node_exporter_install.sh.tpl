#!/bin/bash
set -e

# Update system and install required packages
echo "Updating system and installing required packages..."
sudo apt update && sudo apt install -y wget

# Create Node Exporter user if it doesn't already exist
echo "Creating Node Exporter user..."
if ! id "node_exporter" &>/dev/null; then
  sudo useradd --no-create-home --shell /bin/false node_exporter
fi

# Set the correct version of Node Exporter
NODE_EXPORTER_VERSION="1.6.1"  # Replace this with the version you want to install


# Check if the version is defined
if [ -z "$NODE_EXPORTER_VERSION" ]; then
  echo "Error: NODE_EXPORTER_VERSION is not set."
  exit 1
fi

# Download and install Node Exporter
echo "Downloading and installing Node Exporter version ${NODE_EXPORTER_VERSION}..."
cd /tmp
wget https://github.com/prometheus/node_exporter/releases/download/v${NODE_EXPORTER_VERSION}/node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64.tar.gz
tar xvfz node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64.tar.gz
cd node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64

# Move Node Exporter binary to the correct location
echo "Moving Node Exporter binary to /usr/local/bin/..."
sudo mv node_exporter /usr/local/bin/

# Clean up the temporary files
echo "Cleaning up temporary files..."
sudo rm -rf /tmp/node_exporter*

# Set ownership of the binary
echo "Setting ownership of the binary to node_exporter user..."
# Ensure correct permissions
sudo chown node_exporter:node_exporter /usr/local/bin/node_exporter
sudo chmod +x /usr/local/bin/node_exporter

# Create systemd service file for Node Exporter
echo "Creating systemd service file..."
cat <<EOF | sudo tee /etc/systemd/system/node_exporter.service > /dev/null
[Unit]
Description=Prometheus Node Exporter
After=network.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
Restart=on-failure
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target
EOF

# Check if the systemd service file was created successfully
if [ ! -f /etc/systemd/system/node_exporter.service ]; then
  echo "Error: systemd service file for Node Exporter was not created!"
  exit 1
else
  echo "Systemd service file created successfully."
fi

# Reload systemd to pick up the new service file
echo "Reloading systemd..."
sudo systemctl daemon-reload

# Start Node Exporter
echo "Starting Node Exporter service..."
sudo systemctl start node_exporter

# Enable Node Exporter to start on boot
echo "Enabling Node Exporter to start on boot..."
sudo systemctl enable node_exporter

# Check the status of Node Exporter
echo "Checking the status of Node Exporter service..."
sudo systemctl status node_exporter

# Verify Node Exporter version
echo "Verifying Node Exporter version..."
/usr/local/bin/node_exporter --version
