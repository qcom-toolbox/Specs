[![Java](https://img.shields.io/badge/Java-17%2F21-blue.svg?logo=java)](https://adoptium.net/)
[![FreeBSD](https://img.shields.io/badge/FreeBSD-supported-red.svg?logo=freebsd)](https://www.freshports.org/java/openjdk17/)
[![GhostBSD](https://img.shields.io/badge/GhostBSD-supported-3f5cff.svg?logo=ghost)](https://www.ghostbsd.org/)
[![Linux](https://img.shields.io/badge/Linux-supported-green.svg?logo=linux)](https://openjdk.java.net/)
[![macOS](https://img.shields.io/badge/macOS-supported-lightgrey.svg?logo=apple)](https://adoptium.net/)
[![Windows](https://img.shields.io/badge/Windows-supported-blue?logo=windows&logoColor=white)](https://adoptium.net/)
[![Arch Linux](https://img.shields.io/badge/Arch-Linux-blue?logo=arch-linux&logoColor=white)](https://archlinux.org/packages/?q=openjdk)
[![Debian](https://img.shields.io/badge/Debian-supported-a80030.svg?logo=debian)](https://packages.debian.org/search?keywords=openjdk)
[![Fedora](https://img.shields.io/badge/Fedora-supported-294172.svg?logo=fedora)](https://src.fedoraproject.org/rpms/java-17-openjdk)
[![Gentoo](https://img.shields.io/badge/Gentoo-supported-54487a.svg?logo=gentoo)](https://packages.gentoo.org/packages/dev-java/openjdk)

---

# Specs :

### A Java application that displays detailed information about your PC's hardware.

![img.png](img.png)

# Features :

**CPU Details :** View your processor's model, physical and logical core count.  
**GPU Information :** See your graphics processor name and VRAM capacity.  
**RAM Usage :** Shows total, used, and free memory, without cached files.  
**Operating System :** Displays OS name and version.  
**Auto Refresh :** Set intervals to automatically update displayed information.

# Minimum Requirements :

🖥️ OS : Windows 7 or better / Linux 4.4 or better / Mac OS 10.11 or better  
⚙️ CPU : 64 bits CPU  
💾 RAM : 512 MO of RAM  
💿 Storage : 512 Mo of free space  
☕ Java : JDK 17 or better

# Project Status :

### Legend :
- ✅ Yes
- ❌ No
- ⚠️ Partial or Special Case
- 🟧 Not Available/Unknown

| OS                | Launch    | Installer | Standalone Version   | Icon | Stress Test | OS | CPU  | RAM  | VRAM  |
|-------------------|-----------|-----------|----------------------|------|-------------|----|------|------|-------|
| Windows           | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| Arch Linux        | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| Ubuntu            | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| Debian            | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| Fedora            | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| Gentoo Linux      | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| Mac OS            | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |
| BSD (Should Work) | ✅        | ✅        | ✅                  | ✅   | ✅          | ✅ | ✅  | ✅   | ✅    |

# Validation :

- the validation feature is a feature that allows you to display information from multiple PCs to create statistics.
![img_1.png](img_1.png)
- This feature requires a server to be used.  
- currently there is no official server available, but you can create one by using the guide [how to create you own Specs Server](https://github.com/enzo-quirici/Specs-Server/).

# Wayland USER :

- At least on Arch Linux the app may be completly white launch it with the argument :
```_JAVA_AWT_WM_NONREPARENTING=1 GDK_BACKEND=x11 java -jar Specs.jar```

# dependency :

## libjpeg turbo 8 :

This is a dependency that may be necessary to install the .deb file on certain Linux distributions based on Debian.

### Debian :

```bash
wget http://mirrors.kernel.org/ubuntu/pool/main/libj/libjpeg-turbo/libjpeg-turbo8_2.1.2-0ubuntu1_amd64.deb  
sudo apt install ./libjpeg-turbo8_2.1.2-0ubuntu1_amd64.deb
```

## glxinfo :  

- GLXINFO has been replaced with OSHI GLXINFO is now optional.  

- To enable GPU and VRAM information retrieval on Linux, this program requires `glxinfo`. Below are the instructions for installing `glxinfo` on Debian, Ubuntu, Fedora, Arch Linux, and Gentoo.  

### Debian / Ubuntu :
On Debian or Ubuntu, `glxinfo` is part of the `mesa-utils` package :
```bash
sudo apt-get update
sudo apt-get install mesa-utils
```
### Fedora :
On Fedora, you can install glxinfo with the mesa-demos package :
```bash
sudo dnf install mesa-demos
```
### Arch Linux :
On Arch Linux, glxinfo is provided by the mesa-demos package :
```bash
sudo pacman -S mesa-demos
```
### Gentoo :
On Gentoo, you can install glxinfo by emerging the mesa-progs package :
```
sudo emerge --ask mesa-progs -av
```
### Verifying the Installation :
To confirm that glxinfo is installed correctly, run :
```bash
glxinfo | grep "OpenGL version"
```
If glxinfo returns OpenGL version information, the installation was successful.

# Gneu Gneu On ne peut pas voir le stockage. / The app is ugly.

it's a choice, really not, but really not that I don't have the skills to do better.

Try Specs Plus By Nat 649 : https://github.com/nat649/SpecsPlus

## Star History

<a href="https://www.star-history.com/?repos=qcom-toolbox%2FSpecs&type=date&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=qcom-toolbox/Specs&type=date&theme=dark&legend=top-left" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=qcom-toolbox/Specs&type=date&legend=top-left" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=qcom-toolbox/Specs&type=date&legend=top-left" />
 </picture>
</a>

## Assets

- CPU icon created by Freepik - Flaticon
  https://www.flaticon.com/free-icon/cpu_1892518
