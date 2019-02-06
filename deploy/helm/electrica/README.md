# Electrica

[Electrica.io](https://electrica.io/) is a platform for providing a uniform way to integrate systems between each other.

## Introduction

This chart bootstraps all Electrica components deployment on a [Kubernetes](http://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Chart Details

This chart can install multiple istio components as subcharts:
- postgres
- rabbit
- swagger
- userService
- connectorHubService
- connectorService
- invokerService
- webhookService
- websocketService
- metricService

To enable or disable each component, change the corresponding `enabled` flag.

## Prerequisites

- Kubernetes 1.9 or newer cluster with RBAC (Role-Based Access Control) enabled is required
- Helm 2.7.2 or newer or alternately the ability to modify RBAC rules is also required

## Resources Required

The chart deploys pods that consume minimum resources as specified in the resources configuration parameter.

## Installing the Chart

1. If a Istio has not already been installed, install one:
https://istio.io/docs/setup/kubernetes/helm-install/

2. Build package using Helm
```
$ helm package deploy/helm/electrica
```

3. Install the package to K8s cluster
```
$ helm install electrica-<VESION>.tgz --name electrica --namespace electrica-system
```

## Configuration

The Helm chart ships with reasonable defaults.  There may be circumstances in which defaults require overrides.
To override Helm values, use `--set key=value` argument during the `helm install` command.  Multiple `--set` operations may be used in the same Helm operation.

Helm charts expose configuration options which are currently in alpha.  The currently exposed options are explained in the following table:

| Parameter | Description | Values | Default |
| --- | --- | --- | --- |
| `global.hub` | Specifies the HUB for most images used by Electrica | registry/namespace | `docker.io/istio` |
| `global.tag` | Specifies the TAG for most images used by Electrica | valid image tag | `latest` |
| `global.imagePullPolicy` | Specifies the image pull policy | valid image pull policy | `Always` |
| `global.host.api` | Specifies the Electrica service API host to be used | valid host | `api.electrica.io` |
| `postgres.enabled` | Specifies whether `postgres` should be installed | true/false | `true` |
| `rabbit.enabled` | Specifies whether `rabbit` should be installed | true/false | `true` |
| `swagger.enabled` | Specifies whether `swagger` should be installed | true/false | `true` |
| `userService.enabled` | Specifies whether `userService` should be installed | true/false | `true` |
| `connectorHubService.enabled` | Specifies whether `connectorHubService` should be installed | true/false | `true` |
| `connectorService.enabled` | Specifies whether `connectorService` should be installed | true/false | `true` |
| `invokerService.enabled` | Specifies whether `invokerService` should be installed | true/false | `true` |
| `webhookService.enabled` | Specifies whether `webhookService` should be installed | true/false | `true` |
| `websocketService.enabled` | Specifies whether `websocketService` should be installed | true/false | `true` |
| `metricService.enabled` | Specifies whether `metricService` should be installed | true/false | `true` |

## Uninstalling the Chart

To uninstall/delete the `electrica` release:
```
$ helm delete electrica
```
The command removes all the Kubernetes components associated with the chart and deletes the release.

To uninstall/delete the `electrica` release completely and make its name free for later use:
```
$ helm delete electrica --purge
```