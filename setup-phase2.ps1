# PAL Transport - Phase 2 Setup Script (Authentication)
Write-Host "🚀 Starting PAL Transport Phase 2 Setup (Authentication)..." -ForegroundColor Green

# Create directory structure
Write-Host "📁 Creating directory structure..." -ForegroundColor Yellow

# Backend structure
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/controller" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/entity" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/dto" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/service" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/config" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/config" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/repos" -Force | Out-Null
New-Item -ItemType Directory -Path "pal-transport/src/main/java/com/pal_transport/application/converter" -Force | Out-Null

# Frontend structure
New-Item -ItemType Directory -Path "fe/pal-transport/src/components" -Force | Out-Null
New-Item -ItemType Directory -Path "fe/pal-transport/src/pages" -Force | Out-Null
New-Item -ItemType Directory -Path "fe/pal-transport/src/context" -Force | Out-Null
New-Item -ItemType Directory -Path "fe/pal-transport/src/services" -Force | Out-Null
New-Item -ItemType Directory -Path "fe/pal-transport/src/config" -Force | Out-Null

Write-Host "✅ Directory structure created" -ForegroundColor Green

# Copy Phase 1 files as base
Write-Host "📋 Copying Phase 1 files as base..." -ForegroundColor Yellow
Copy-Item "../pal-transport-phase-1/*" "." -Recurse -Force
Write-Host "✅ Phase 1 files copied" -ForegroundColor Green

# Create enhanced pom.xml for Phase 2
Write-Host "🔧 Creating enhanced pom.xml for Phase 2..." -ForegroundColor Yellow
$pomContent = @'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.4.3</version>
		<relativePath/>
	</parent>
	<groupId>com.pal-transport</groupId>
	<artifactId>pal-transport</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>pal-transport</name>
	<description>PAL Transport - Phase 2 (Authentication)</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<!-- JWT Dependencies -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.12.3</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.12.3</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.12.3</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>
'@

$pomContent | Out-File -FilePath "pal-transport/pom-phase2.xml" -Encoding UTF8
Write-Host "✅ Enhanced pom.xml created" -ForegroundColor Green

# Create enhanced package.json for Phase 2
Write-Host "📦 Creating enhanced package.json for Phase 2..." -ForegroundColor Yellow
$packageContent = @'
{
  "name": "pal-transport-frontend",
  "private": true,
  "version": "0.0.2",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "lint": "eslint .",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-router-dom": "^6.26.2",
    "@react-oauth/google": "^0.12.2",
    "class-variance-authority": "^0.7.1",
    "clsx": "^2.1.1",
    "lucide-react": "^0.462.0",
    "tailwind-merge": "^2.5.2",
    "tailwindcss-animate": "^1.0.7",
    "react-hook-form": "^7.53.0",
    "zod": "^3.23.8",
    "@hookform/resolvers": "^3.10.0"
  },
  "devDependencies": {
    "@types/node": "^22.5.5",
    "@types/react": "^18.3.3",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react-swc": "^3.5.0",
    "autoprefixer": "^10.4.20",
    "eslint": "^9.9.0",
    "postcss": "^8.4.47",
    "tailwindcss": "^3.4.11",
    "typescript": "^5.5.3",
    "vite": "^5.4.1"
  }
}
'@

$packageContent | Out-File -FilePath "fe/pal-transport/package-phase2.json" -Encoding UTF8
Write-Host "✅ Enhanced package.json created" -ForegroundColor Green

# Create README for Phase 2
Write-Host "📖 Creating README for Phase 2..." -ForegroundColor Yellow
$readmeContent = @'
# PAL Transport - Phase 2: Authentication System

This is the second phase of the PAL Transport application deployment, adding authentication capabilities.

## What is New in Phase 2

### Backend Authentication
- JWT-based authentication system
- Google OAuth integration
- User management and registration
- Security configuration
- Password encryption
- Token refresh mechanism

### Frontend Authentication
- Login form with validation
- Google OAuth button
- Authentication context
- Protected routes
- User profile management
- Logout functionality

## Setup Instructions

### Backend Setup
1. Navigate to `pal-transport/`
2. Copy `pom-phase2.xml` to `pom.xml`
3. Run: `mvn clean install`
4. Run: `mvn spring-boot:run`

### Frontend Setup
1. Navigate to `fe/pal-transport/`
2. Copy `package-phase2.json` to `package.json`
3. Run: `npm install`
4. Run: `npm run dev`

## Authentication Endpoints

### Backend API Endpoints:
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Token refresh
- `GET /api/auth/profile` - Get user profile
- `POST /api/auth/logout` - User logout
- `GET /api/auth/google` - Google OAuth initiation
- `GET /api/auth/callback` - Google OAuth callback

### Frontend Routes:
- `/login` - Login page
- `/register` - Registration page
- `/profile` - User profile (protected)
- `/oauth/callback` - OAuth callback handler

## Testing Checklist

### Backend Testing:
- [ ] JWT token generation works
- [ ] User registration works
- [ ] User login works
- [ ] Google OAuth integration works
- [ ] Token refresh works
- [ ] Protected endpoints work
- [ ] Logout functionality works

### Frontend Testing:
- [ ] Login form renders correctly
- [ ] Form validation works
- [ ] Google OAuth button works
- [ ] Authentication context works
- [ ] Protected routes redirect properly
- [ ] User profile displays correctly
- [ ] Logout clears session

### Security Testing:
- [ ] Passwords are encrypted
- [ ] JWT tokens are secure
- [ ] Protected routes are secure
- [ ] OAuth flow is secure
- [ ] Session management works

## Configuration

### Required Environment Variables:
- `JWT_SECRET` - Secret key for JWT signing
- `GOOGLE_CLIENT_ID` - Google OAuth client ID
- `GOOGLE_CLIENT_SECRET` - Google OAuth client secret
- `GOOGLE_REDIRECT_URI` - OAuth redirect URI

## Next Phase

Phase 3 will include core business entities (Clients, Drivers, Vehicles).
'@

$readmeContent | Out-File -FilePath "README.md" -Encoding UTF8
Write-Host "✅ README created" -ForegroundColor Green

Write-Host ""
Write-Host "🎉 Phase 2 setup completed!" -ForegroundColor Green
Write-Host ""
Write-Host "📁 Phase 2 files are in: pal-transport-phase-2" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Next steps:" -ForegroundColor Yellow
Write-Host "1. Review the authentication files"
Write-Host "2. Test the authentication system"
Write-Host "3. Commit to the new repository"
Write-Host "4. Create pull request for testing"
Write-Host ""
Write-Host "🔗 Repository: https://github.com/Nitinpal-18/PAL-TRANSPORT-MAIN.git" -ForegroundColor Cyan
Write-Host "" 