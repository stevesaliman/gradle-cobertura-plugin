package com.orbitz.gradle.cobertura

import java.io.File;

import org.gradle.StartParameter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.project.IProjectFactory;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.project.ServiceRegistryFactory;
import org.gradle.initialization.DefaultProjectDescriptor;
import org.gradle.initialization.DefaultProjectDescriptorRegistry;
import org.gradle.invocation.DefaultGradle;
import org.gradle.testfixtures.ProjectBuilder 
import org.gradle.testfixtures.ProjectBuilder.TestTopLevelBuildServiceRegistry;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.fail;

class CoberturaPluginTest {

    Project project
    
    @BeforeMethod
    public void setUp() throws Exception {
        project = ProjectBuilder.builder().build()
        project.version = '1.0'
    }
    
    @AfterMethod
    public void tearDown() {
        project.projectDir.deleteDir()
    }
    
    @Test
    void canApplyPlugin() {
        project.rootProject.buildscript.repositories {
            mavenCentral()
        }

        ServiceRegistryFactory topLevelRegistry = project.gradle.serviceRegistryFactory        
        File childProjectDir = new File(project.projectDir, 'child')
        final File homeDir = new File(childProjectDir, "gradleHome");
        StartParameter startParameter = new StartParameter();
        startParameter.setGradleUserHomeDir(new File(childProjectDir, "userHome"));
        DefaultProjectDescriptor projectDescriptor = new DefaultProjectDescriptor(null, "child", childProjectDir, new DefaultProjectDescriptorRegistry());
        projectDescriptor.path = ':child'
        ProjectInternal childProject = topLevelRegistry.get(IProjectFactory.class).createProject(projectDescriptor, project, project.gradle);

        project.childProjects['child'] = childProject
        project.apply plugin: CoberturaPlugin
        childProject.apply plugin: CoberturaPlugin
        assert project.plugins.hasPlugin(CoberturaPlugin)
        assert project.tasks.getByName('cobertura')
        try {
            assert project.buildscript.configurations['cobertura']
        } catch (UnknownConfigurationException uce) {
            fail()
            throw uce
        }
        def e = null
        try {
            assert childProject.buildscript.configurations['cobertura']
        } catch (UnknownConfigurationException uce) {
            e = uce
        }
        assert e != null
        try {
            assert childProject.rootProject.buildscript.configurations['cobertura']
        } catch (UnknownConfigurationException uce) {
            fail()
            throw uce
        }
    }

}
