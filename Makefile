all: deploy-test

deploy-test:
	@mvn clean install -DskipTests=true -Ddebug=2 -T1.0C && adb install -r app/target/monkeywrapper-app-*-aligned.apk


.PHONY: deploy-test
