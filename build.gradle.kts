plugins {
	java
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenLocal()
	maven("https://repo.nukkitx.com/main/")
	jcenter()
}

dependencies {
	compileOnly("org.cloudburstmc:cloudburst-server:1.0.0-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:17.0.0")
}
