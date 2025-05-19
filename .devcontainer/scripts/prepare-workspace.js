// .devcontainer/scripts/prepare-workspace.js
const fs = require('fs');
const path = require('path');

// Create temp directory structure
const tmpDir = path.join('.devcontainer', 'workspace_tmp');
const tmpSrcMainDir = path.join(tmpDir, 'src', 'main');
const tmpSrcTestDir = path.join(tmpDir, 'src', 'test');

// Ensure directories exist
fs.mkdirSync(tmpSrcMainDir, { recursive: true });
fs.mkdirSync(tmpSrcTestDir, { recursive: true });

// Copy directories if they exist
function copyDirIfExists(src, dest) {
  if (!fs.existsSync(src)) {
    console.log(`Source directory ${src} doesn't exist, skipping`);
    return;
  }

  // Create destination directory if it doesn't exist
  if (!fs.existsSync(dest)) {
    fs.mkdirSync(dest, { recursive: true });
  }

  // Copy files and subdirectories
  const entries = fs.readdirSync(src, { withFileTypes: true });
  for (const entry of entries) {
    const srcPath = path.join(src, entry.name);
    const destPath = path.join(dest, entry.name);

    if (entry.isDirectory()) {
      fs.mkdirSync(destPath, { recursive: true });
      copyDirIfExists(srcPath, destPath);
    } else {
      fs.copyFileSync(srcPath, destPath);
    }
  }
  console.log(`Copied ${src} to ${dest}`);
}

// Try to copy Java directories
const mainJavaDir = path.join('src', 'main', 'java');
const testJavaDir = path.join('src', 'test', 'java');
const tmpMainJavaDir = path.join(tmpSrcMainDir, 'java');
const tmpTestJavaDir = path.join(tmpSrcTestDir, 'java');

copyDirIfExists(mainJavaDir, tmpMainJavaDir);
copyDirIfExists(testJavaDir, tmpTestJavaDir);

console.log('Workspace preparation complete!');
